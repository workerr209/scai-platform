package com.springcore.ai.scai_platform.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FormMasterDeleteDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1367972494158538532L;
	private List<Long> ids;

}
