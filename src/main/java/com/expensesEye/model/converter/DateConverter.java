package com.expensesEye.model.converter;

import static org.springframework.util.StringUtils.isEmpty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

/**
 * This class implements {@link Converter} and it is used to convert a String to
 * a Date, constructing given the date format
 */
public class DateConverter implements Converter<String, Date> {

	private static Logger logger = LoggerFactory.getLogger(DateConverter.class);

	private final SimpleDateFormat dateFormatter;
	private final String format;

	public DateConverter(String format) {
		this.format = format;
		this.dateFormatter = new SimpleDateFormat(format);
	}

	@Override
	public Date convert(String source) {
		if (isEmpty(source))
			return null;
		try {
			return dateFormatter.parse(source);
		} catch (ParseException e) {
			logger.error(String.format("Unable to convert String '%s' to Date using the following format '%s'", source,
					format));
			return null;
		}
	}

	public String getFormat() {
		return format;
	}

}
