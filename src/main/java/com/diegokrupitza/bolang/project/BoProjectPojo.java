package com.diegokrupitza.bolang.project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author Diego Krupitza
 * @version 1.0
 * @date 04.08.21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoProjectPojo {

    private String name;

    private String description;

    private String main;

    private Map<String, String> modules;

    private Map<String, String> params;

}
