module com.example.casinoroyale {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires mysql.connector.j;
    requires java.sql;
    requires javafx.media;
    requires annotations;
    requires java.desktop;

    opens com.example.casinoroyale to javafx.fxml;
    exports com.example.casinoroyale;
}