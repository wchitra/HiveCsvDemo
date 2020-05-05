package com.example.demo.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

import com.example.demo.models.Member;

public class MemberFirstNameItemProcessor implements ItemProcessor<Member, Member> {

  private static final Logger log = LoggerFactory.getLogger(MemberFirstNameItemProcessor.class);

  @Override
  public Member process(final Member member) throws Exception {
    final String firstName = member.getFirstName().toUpperCase() + "++";

    // carry the attributes thru
    final Member transformedPerson = new Member();
    transformedPerson.setId(member.getId());
    transformedPerson.setFirstName(firstName);
    transformedPerson.setLastName(member.getLastName());
    transformedPerson.setProvider(member.getProvider());

    log.info("Converting (" + member + ") into (" + transformedPerson + ")");

    return transformedPerson;
  }

}