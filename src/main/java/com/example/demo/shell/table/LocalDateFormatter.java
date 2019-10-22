package com.example.demo.shell.table;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.shell.table.Formatter;

public class LocalDateFormatter implements Formatter {

	private String pattern;

	public LocalDateFormatter(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public String[] format(Object value) {
		LocalDate localDate = (LocalDate) value;
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		return new String[] { format.format(date) };
	}
}
