package com.example.demo.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.example.demo.models.Member;

public class MemberRowMapper implements RowMapper<Member> {

	public static final String ID_COLUMN = "id";
    public static final String FIRST_NAME_COLUMN = "first";
    public static final String LAST_NAME_COLUMN = "last";
    public static final String PROVIDER_COLUMN = "provider";
    
	@Override
	public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
		Member member = new Member();

        member.setId(rs.getInt(ID_COLUMN));
        member.setFirstName(rs.getString(FIRST_NAME_COLUMN));
        member.setLastName(rs.getString(LAST_NAME_COLUMN));
        member.setProvider(rs.getString(PROVIDER_COLUMN));

        return member;
	}
}
