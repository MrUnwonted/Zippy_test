package com.camerinfolks.model.core.constants;
/**
 * @author Arjun
 */
public interface AnnotationValues {

	int CODE_LENGTH = 50;

	int NAME_LENGTH = 100;

	int DESCRIPTION_SMALL_LENGTH = 255;

	int DESCRIPTION_LENGTH = 500;

	int CREATEDBY_LENGTH = 100;

	int PHONE_NO_LENGTH = 20;

	int EMAIL_LENGTH = 100;

	int REMARKS_LENGTH = 1000;

	int CURRENCY_LENGTH = 3;

	int DESCRIPTION_BIG_LENGTH = 5000;

	String SCHEMA_NAME = "Life_QC";

	interface Generator {
		int ALLOCATION_SIZE = 1;
	}

}
