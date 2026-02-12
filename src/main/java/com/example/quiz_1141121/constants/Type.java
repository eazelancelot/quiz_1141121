package com.example.quiz_1141121.constants;

import org.springframework.util.StringUtils;

public enum Type {
	
	SINGLE("Single"),//
	MULTI("Multi"),//
	TEXT("Text");

	private String type;

	private Type(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public static boolean check(String input) {
		if(!StringUtils.hasText(input)) {
			return false;
		}
//		if(input.equalsIgnoreCase(SINGLE.getType()) 
//				|| input.equalsIgnoreCase(MULTI.getType())
//				|| input.equalsIgnoreCase(TEXT.getType())) {
//			return true;
//		}
		
		/* values() 取得的是這個 enum 中所有的列舉對象 */
		for(Type type :  values()) {
			if(input.equalsIgnoreCase(type.getType())) {
				return true;
			}
		}	
		return false;
	}
	
	public static boolean isChoiceType(String input) {
//		if(input.equalsIgnoreCase(SINGLE.getType())
//				|| input.equalsIgnoreCase(MULTI.getType())) {
//			return true;
//		}
//		return false;
		return input.equalsIgnoreCase(SINGLE.getType())
				|| input.equalsIgnoreCase(MULTI.getType());
	}

}
