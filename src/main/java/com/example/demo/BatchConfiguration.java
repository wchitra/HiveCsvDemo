package com.example.demo;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.example.demo.listeners.JobCompletionNotificationListener;
import com.example.demo.mappers.MemberRowMapper;
import com.example.demo.models.Member;
import com.example.demo.processors.MemberFirstNameItemProcessor;
import com.example.demo.processors.MemberLastNameItemProcessor;
import com.example.demo.readers.HiveItemReader;
import com.example.demo.writer.FooterWriter;
import com.example.demo.writer.HeaderWriter;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	final private String datetimeFormat = "uuuuMMddHHmmss";
	final private String delimiter = "|";
	final private String timestamp = ZonedDateTime
			.now( ZoneId.systemDefault() )
			.format( DateTimeFormatter.ofPattern( datetimeFormat ) );

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	public HiveItemReader<Member> reader() {
		HiveItemReader<Member> reader = new HiveItemReader<Member>();
		reader.setUrl("jdbc:hive2://localhost:10000/test_members");
		reader.setSql("select * from members");
		reader.setRowMapper(new MemberRowMapper());
		return reader;
	}
	
	@Bean
	public CompositeItemProcessor<Member, Member> compositeProcessor() {
		return new CompositeItemProcessorBuilder<Member, Member>()
				.delegates(firstNameProcessor(),lastNameProcessor())
				.build();
	}

	@Bean
	public MemberFirstNameItemProcessor firstNameProcessor() {
		return new MemberFirstNameItemProcessor();
	}
	
	@Bean MemberLastNameItemProcessor lastNameProcessor() {
		return new MemberLastNameItemProcessor();
	}

	
	@Bean
	public CompositeItemWriter<Member> compositeWriter() throws Exception {
		return new CompositeItemWriterBuilder<Member>()
				.delegates(firstNameWriter(), lastNameWriter(), providerWriter())
				.build();
	}

	@Bean
	public FlatFileItemWriter<Member> firstNameWriter() throws Exception {
		BeanWrapperFieldExtractor<Member> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(new String[] {"id", "firstName"});
		fieldExtractor.afterPropertiesSet();

		DelimitedLineAggregator<Member> lineAggregator = new DelimitedLineAggregator<>();
		lineAggregator.setDelimiter(delimiter);
		lineAggregator.setFieldExtractor(fieldExtractor);

		return new FlatFileItemWriterBuilder<Member>()
					.name("firstNameWriter")
					.resource(new FileSystemResource(fileNameWithTimeStamp("first.csv")))
					.lineAggregator(lineAggregator)
					.headerCallback(headerCallback())
					.footerCallback(footerCallback())
					.build();
	}
	
	@Bean
	public FlatFileItemWriter<Member> lastNameWriter() throws Exception {
		BeanWrapperFieldExtractor<Member> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(new String[] {"id", "lastName"});
		fieldExtractor.afterPropertiesSet();

		DelimitedLineAggregator<Member> lineAggregator = new DelimitedLineAggregator<>();
		lineAggregator.setDelimiter(delimiter);
		lineAggregator.setFieldExtractor(fieldExtractor);

		return new FlatFileItemWriterBuilder<Member>()
					.name("lastNameWriter")
					.resource(new FileSystemResource(fileNameWithTimeStamp("last.csv")))
					.lineAggregator(lineAggregator)
					.headerCallback(headerCallback())
					.footerCallback(footerCallback())
					.build();
	}
	
	@Bean
	public FlatFileItemWriter<Member> providerWriter() throws Exception {
		BeanWrapperFieldExtractor<Member> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(new String[] {"id", "provider"});
		fieldExtractor.afterPropertiesSet();

		DelimitedLineAggregator<Member> lineAggregator = new DelimitedLineAggregator<>();
		lineAggregator.setDelimiter(delimiter);
		lineAggregator.setFieldExtractor(fieldExtractor);

		return new FlatFileItemWriterBuilder<Member>()
					.name("providerWriter")
					.resource(new FileSystemResource(fileNameWithTimeStamp("provider.csv")))
					.lineAggregator(lineAggregator)
					.headerCallback(headerCallback())
					.footerCallback(footerCallback())
					.build();
	}
	
	@Bean
	public HeaderWriter headerCallback() {
		return new HeaderWriter();
	}
	
	@Bean
	public FooterWriter footerCallback() {
		return new FooterWriter();
	}


	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory
				.get("importUserJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(step1)
				.end().build();
	}

	@Bean
	public Step step1() throws Exception {
		return stepBuilderFactory
				.get("step1")
				.<Member, Member>chunk(10)
				.reader(reader())
				.processor(compositeProcessor())
				.writer(compositeWriter())
				.build();
	}
	
	
	private String fileNameWithTimeStamp(String filename) {
		return filename + timestamp;
	}

}
