package com.zhaoyg.generator;


import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;

import java.util.Collections;

/**
 * @author zhao
 * @date 2022/8/11
 */
public class AutoGenerator {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/z_coupon";
    private static final String USER = "root";
    private static final String PWD = "rootroot";
    private static final String FINAL_PROJECT_PATH = "/Users/z/Desktop";

    public static void main(String[] args) {
        FastAutoGenerator.create(URL, USER, PWD)
                .globalConfig(builder ->
                        builder.author("zhao")
                                .enableSwagger()
                                .fileOverride()
                                .disableOpenDir()
                                .outputDir(FINAL_PROJECT_PATH + "/src/main/java")
                )
                .packageConfig(builder ->
                        builder.parent("com.zhaoyg")
                                .entity("model.entity")
                                .other("model.dto")
                                .pathInfo(Collections.singletonMap(OutputFile.mapperXml, FINAL_PROJECT_PATH + "/src/main/resources/mapper"))

                )
                .strategyConfig(builder -> {
                    builder.entityBuilder().enableLombok();
                    builder.serviceBuilder().formatServiceFileName("%sService").formatServiceImplFileName("%sServiceImpl");

                })
                .execute();

    }

}
