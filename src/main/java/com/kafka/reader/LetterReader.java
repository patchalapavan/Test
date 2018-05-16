package com.kafka.reader;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.kafka.processors.LetterProcessors;
import com.kafka.util.KafkaUtil;
import com.kafka.vo.LetterVo;

//@Component
public class LetterReader extends RouteBuilder {

	@Value("${camel.file.input}")
	private String inputFile;
	@Autowired
	private KafkaUtil util;

	@Override
	public void configure() throws Exception {

		BindyDataFormat bindyDataFormat = util.bindyFormat();
		bindyDataFormat.setClassType(LetterVo.class);

		// Read the file
		from("file://" + inputFile + "?fileName=letter.txt&noop=true").log("reads file").split().tokenize("\n", 1, true)
				.unmarshal(bindyDataFormat).process(new LetterProcessors()).log("toKafkaTopic")
				.to(util.buildKafkaEndpointUri("kafka:redcard-enrollment-archives-letter"));

	}

}
