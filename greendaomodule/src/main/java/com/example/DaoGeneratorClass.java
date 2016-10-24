package com.example;
import java.io.IOException;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DaoGeneratorClass {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1,"com.example.sachin.lecturereminder.dbModel");

        Entity entity = schema.addEntity("classData");

        entity.addIdProperty().primaryKey().autoincrement();

        entity.addStringProperty("name").notNull();
        entity.addStringProperty("topic").notNull();
        entity.addStringProperty("professor").notNull();
        entity.addDateProperty("dateTime").notNull();
        entity.addStringProperty("location").notNull();
        new DaoGenerator().generateAll(schema,"./app/src/main/java");
    }
}
