package com.mk.eap.entity.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JsonSelectorDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5048142450855207017L;

	private String selectFieldsJson;

	private List<String> fields;

	private HashMap<String, JsonSelectorDto> subFields = new HashMap<String, JsonSelectorDto>();

	/**
	 * id,code,department:{code,name},name,person:{*,family:{name},name,education:{name},ts},ts
	 * 
	 * @param jsonify
	 */
	public JsonSelectorDto(String jsonify) {
		this.translateJsonify(jsonify);
	}

	public JsonSelectorDto() {
	}

	static String EMPTY = "";
	static String ALL = "*";

	public void translateJsonify(String jsonify) {
		this.selectFieldsJson = jsonify;
		this.fields = new ArrayList<String>();
		if (jsonify == null) {
			return;
		}
		String selectFields = jsonify.replace("\r", "").replace("\n", "").replace("\t", "");
		String[] keys = selectFields.split(":");
		int level = 0;
		String preKey = EMPTY;
		String preFields = EMPTY;
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i].trim();
			if (EMPTY.equals(preFields)) {
				preFields = key;
			} else {
				preFields += ":" + key;
			}
			if (key.indexOf("{") != -1) {
				level = level + CountChar(key, "[{]") - CountChar(key, "[}]");
			}
			if (level == 0) {
				if (this.fields != null) {
					boolean isLast = i == keys.length - 1;
					addFields(key, !isLast);
				}
				if (!EMPTY.equals(preKey)) {
					String subJson = BetweenChar(preFields, "{", "}");
					JsonSelectorDto subDto = new JsonSelectorDto(subJson);
					this.subFields.put(preKey, subDto);
					preFields = EMPTY;
				}
				preKey = key.substring(key.lastIndexOf(",") + 1).trim();
			}
		}
	}

	private static int CountChar(String srcText, String findText) {
		int count = 0;
		Pattern p = Pattern.compile(findText);
		Matcher m = p.matcher(srcText);
		while (m.find()) {
			count++;
		}
		return count;
	}

	private static String BetweenChar(String str, String key1, String key2) {
		return str.substring(str.indexOf(key1) + 1, str.lastIndexOf(key2));
	}

	public void addFields(String key, boolean clearLastItem) {
		String[] fs = key.substring(key.lastIndexOf("}") + 1).split(",");
		if (clearLastItem) {
			fs[fs.length - 1] = EMPTY;
		}
		for (String f : fs) {
			String field = f.trim();
			if (this.fields != null && !EMPTY.equals(field)) {
				this.fields.add(field);
			}
			if (ALL.equals(field)) {
				this.fields = null;
			}
		}
	}

}
