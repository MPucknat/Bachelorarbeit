/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.example;

import de.example.data.OCPI.LocationOCPI;
import de.example.data.OICP.AuthenticationMode;
import de.example.data.OICP.LocationOICP;
import de.example.data.OICP.PaymentOption;
import de.example.data.OICP.ValueAddedService;
import de.example.data.finalLocation.*;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.formats.json.JsonDeserializationSchema;
import org.apache.flink.formats.json.JsonSerializationSchema;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class DataStreamJob {

	private static final String KAFKA_SERVER = "localhost:9092";
	private static final String GROUP_ID = "flink-stream";

	private static final String OCPI_TOPIC = "ocpi-topic";
	private static final String OICP_TOPIC = "oicp-topic";
	private static final String OUTPUT_TOPIC = "output-topic";

	public static void main(String[] args) throws Exception {

		// Sets up the execution environment, which is the main entry point
		// to building Flink applications.
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.enableCheckpointing(1000, CheckpointingMode.EXACTLY_ONCE);

		// Setup Kafka Source
		KafkaSource<LocationOCPI> OCPISource = KafkaSource.<LocationOCPI>builder()
				.setBootstrapServers(KAFKA_SERVER)
				.setGroupId(GROUP_ID)
				.setTopics(OCPI_TOPIC)
				.setStartingOffsets(OffsetsInitializer.earliest())
				.setBounded(OffsetsInitializer.latest())
				.setValueOnlyDeserializer(new JsonDeserializationSchema<>(LocationOCPI.class))
				.build();

		KafkaSource<LocationOICP> OICPSource = KafkaSource.<LocationOICP>builder()
				.setBootstrapServers(KAFKA_SERVER)
				.setGroupId(GROUP_ID)
				.setTopics(OICP_TOPIC)
				.setStartingOffsets(OffsetsInitializer.earliest())
				.setBounded(OffsetsInitializer.latest())
				.setValueOnlyDeserializer(new JsonDeserializationSchema<>(LocationOICP.class))
				.build();

		DataStream<LocationOCPI> ocpiSource = env.fromSource(OCPISource, WatermarkStrategy.noWatermarks(),"OCPISource");
		DataStream<LocationOICP> oicpStream = env.fromSource(OICPSource, WatermarkStrategy.noWatermarks(), "OICPSource");

		DataStream<LocationFinal> finalOCPIStream = ocpiSource.map( value -> LocationFinal.builder()
				.operatorID(value.getParty_id())
				.poolID(value.getId())
				.operatorName(value.getCountry_code() + "*" + value.getParty_id())
				.open24Hours(value.getOpening_times().isTwentyfourseven())
				.lastUpdate(value.getLast_updated())
				.address(Address.builder()
						.country(value.getCountry())
						.city(value.getCity())
						.address(value.getAddress())
						.postalCode(value.getPostal_code())
						.timeZone(value.getTime_zone())
						.build())
				.coordinates(GeoCoordinates.builder()
						.latitude(value.getCoordinates().getLatitude())
						.longitude(value.getCoordinates().getLongitude()).build())
				.evses(value.getEvses().stream().map(evse -> Evse.builder()
						.evseID(evse.getEvse_id())
						.connectors(evse.getConnectors().stream().map(connector -> Connector.builder()
								.powerType(connector.getPower_type().toString())
								.voltage(connector.getMax_voltage())
								.amperage(connector.getMax_amperage())
								.power(connector.getMax_electric_power())
								.build()).collect(Collectors.toList()))
						.capabilities(evse.getCapabilities().stream().map(Enum::toString).collect(Collectors.toList()))
						.accessibility(evse.getParking_restrictions().toString()).build()).collect(Collectors.toList()))
				.build());

		DataStream<LocationFinal> finalOICPStream = oicpStream.map(value -> LocationFinal.builder()
				.operatorID(value.getOperatorID())
				.operatorName(value.getOperatorName())
				.poolID(value.getChargingPoolID())
				.open24Hours(value.isOpen24Hours())
				.lastUpdate(value.getLastUpdate())
				.address(Address.builder()
						.country(value.getAddress().getCountry())
						.city(value.getAddress().getCity())
						.address(value.getAddress().getStreet() + " " + value.getAddress().getHouseNum())
						.postalCode(value.getAddress().getPostalCode())
						.timeZone(value.getAddress().getTimeZone())
						.build())
				.coordinates(GeoCoordinates.builder()
						.latitude(value.getGeoCoordinates().getLatitude())
						.longitude(value.getGeoCoordinates().getLongitude())
						.build())
				.evses(getEvses(value))
				.build());

		Properties props = new Properties();
		props.put("transaction.timeout.ms", 1000);

		KafkaSink<LocationFinal> oicpSink = KafkaSink.<LocationFinal>builder()
				.setBootstrapServers(KAFKA_SERVER)
				.setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
				.setTransactionalIdPrefix("flink-oicp")
				.setKafkaProducerConfig(props)
				.setRecordSerializer(KafkaRecordSerializationSchema.builder()
						.setTopic(OUTPUT_TOPIC)
						.setValueSerializationSchema(new JsonSerializationSchema<LocationFinal>())
						.build())
				.build();

		KafkaSink<LocationFinal> ocpiSink = KafkaSink.<LocationFinal>builder()
				.setBootstrapServers(KAFKA_SERVER)
				.setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
				.setTransactionalIdPrefix("flink-ocpi")
				.setKafkaProducerConfig(props)
				.setRecordSerializer(KafkaRecordSerializationSchema.builder()
						.setTopic(OUTPUT_TOPIC)
						.setValueSerializationSchema(new JsonSerializationSchema<LocationFinal>())
						.build())
				.build();

		finalOCPIStream.sinkTo(ocpiSink);
		finalOICPStream.sinkTo(oicpSink);

		env.execute("Flink Streaming Job");
	}

	private static List<Evse> getEvses(LocationOICP locationOICP) {
		List<Evse> evses = new ArrayList<>();

		evses.add(Evse.builder()
				.evseID(locationOICP.getEvseID())
				.connectors(getConnectors(locationOICP))
				.capabilities(getCapabilities(locationOICP.getAuthenticationModes(), locationOICP.getPaymentOptions(), locationOICP.getValueAddedServices()))
				.accessibility(locationOICP.getAccessibility().toString())
				.build());

		return evses;
	}

	private static List<Connector> getConnectors(LocationOICP locationOICP) {
		List<Connector> connectors = new ArrayList<>();
		connectors.add(Connector.builder()
				.amperage(locationOICP.getChargingFacilities().getAmperage())
				.voltage(locationOICP.getChargingFacilities().getAmperage())
				.power(locationOICP.getChargingFacilities().getPower())
				.powerType(locationOICP.getChargingFacilities().getPowerType().toString())
				.build());

		return connectors;
	}

	private static List<String> getCapabilities(List<AuthenticationMode> authenticationModes, List<PaymentOption> paymentOptions, List<ValueAddedService> valueAddedServices) {
		List<String> capabilities = new ArrayList<>();

		capabilities.addAll(authenticationModes.stream().map(AuthenticationMode::toString).collect(Collectors.toList()));
		capabilities.addAll(paymentOptions.stream().map(PaymentOption::toString).collect(Collectors.toList()));
		capabilities.addAll(valueAddedServices.stream().map(ValueAddedService::toString).collect(Collectors.toList()));

		return capabilities;
	}
}
