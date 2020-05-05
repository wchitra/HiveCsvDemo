package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Member {
	
	@NonNull
	private Integer id;

	@NonNull
	private String lastName;
	
	@NonNull
	private String firstName;
	
	@NonNull
	private String provider;

	@Override
	public String toString() {
		return "firstName: " + this.firstName + ", lastName: " + this.lastName + ", " + this.provider;
	}
}
