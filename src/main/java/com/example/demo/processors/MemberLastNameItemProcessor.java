package com.example.demo.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.example.demo.models.Member;

public class MemberLastNameItemProcessor implements ItemProcessor<Member, Member> {
	
	private static final Logger log = LoggerFactory.getLogger(MemberLastNameItemProcessor.class);
	
	@Override
	public Member process(Member member) throws Exception {
		final String lastName = member.getLastName().toUpperCase() + "~~";

	    // carry the attributes thru
	    final Member transformedPerson = new Member();
	    transformedPerson.setId(member.getId());
	    transformedPerson.setFirstName(member.getFirstName());
	    transformedPerson.setLastName(lastName);
	    transformedPerson.setProvider(member.getProvider());

	    log.info("Converting (" + member + ") into (" + transformedPerson + ")");

	    return transformedPerson;
	}

}
