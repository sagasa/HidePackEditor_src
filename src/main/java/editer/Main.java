package editer;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import editer.controller.RootController;
import io.ModelIO;
import io.PackIO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import localize.LocalizeHandler;
import model.Bone;
import types.attachments.GunCustomizePart;
import types.attachments.ValueChange;
import types.attachments.ValueChange.ChangeType;

public class Main extends Application {
	/** 開いているpath */
	public static String packPath;

	public static void main(String[] arg) throws ScriptException {
		LocalizeHandler.init();
		LocalizeHandler.loadLang();
		LocalizeHandler.setLang("en");
		PackIO.makePack();
		// new MainWindow();
		// launch(arg);

		Gson gson = new Gson();
		GunCustomizePart item = new GunCustomizePart();
		item.change.add(new ValueChange("TEST", ChangeType.ADD_FLOAT, "AAAA"));
		System.out.println(item.change);
		GunCustomizePart item2 = gson.fromJson(gson.toJson(item), GunCustomizePart.class);
		try {
			System.out.println(item2.change);
			for (ValueChange change : item2.change) {
				System.out.println(change.getClass().getField("TYPE").getGenericType());
			}
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}


		System.exit(0);
	}


	private static final Logger log = LogManager.getLogger();
	private static final Point2D STAGE_SIZE = new Point2D(1280, 720);

	public void start(Stage stage) throws Exception {
		String fxmlFile = "/fxml/editer.fxml";
		log.debug("Loading FXML for main view from: {}", fxmlFile);
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
		Parent rootNode = (Parent) loader.load();
		RootController.STAGE = stage;
		log.debug("Showing JFX scene");
		Scene scene = new Scene(rootNode, STAGE_SIZE.getX(), STAGE_SIZE.getY() - 40);
		stage.setTitle("HidePackEditer");
		stage.getIcons().add(new Image("icon/M14_scope.png"));
		stage.setScene(scene);
		stage.setMinHeight(STAGE_SIZE.getY());
		stage.setMinWidth(STAGE_SIZE.getX());
		stage.show();
	}
}
