package controller.editer;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import helper.EditHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import localize.LocalizeHandler;
import types.base.DataBase;
import types.guns.GunData;

public class EditerComponent {
	private static final Logger log = LoggerFactory.getLogger(EditerComponent.class);

	// GunEditer
	public static void writeGunEditer(Pane editer, GunData data) {
		// Cate1
		AnchorPane cate1 = new AnchorPane();
		cate1.setStyle("-fx-background-color: gray;");
		editer.getChildren().add(cate1);
		cate1.getChildren().add(EditNodeBuilder.makeTextSetNode(data, "ITEM_DISPLAYNAME").setListner(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				RootController.INSTANCE.gunList.setItems(RootController.INSTANCE.gunList.getItems().sorted());;
			}
		}).build());

	}

	// ベースサイズは200x24

	public static class EditNodeBuilder {
		private enum EditNodeType {
			Text, Number,
		}

		/** 作成するノードの種類 */
		private EditNodeType Type;

		private EditNodeBuilder(EditNodeType type) {
			Type = type;
		}

		/** 変更の保存対象 */
		private DataBase Data;
		/** 保存するフィールド名 */
		private String Field;
		/** 変更通知リスナー */
		private ChangeListener<?>[] Listener;

		public static EditNodeBuilder makeTextSetNode(DataBase data, String field) {
			EditNodeBuilder builder = new EditNodeBuilder(EditNodeType.Text);
			builder.Data = data;
			builder.Field = field;
			// Typeチェック
			if (!EditHelper.getType(data, field).isAssignableFrom(String.class)) {
				log.warn(field + "is not Text field");
			}
			return builder;
		}

		public EditNodeBuilder setListner(ChangeListener<?>... listener) {
			Listener = listener;
			return this;
		}

		private double sizeX = 200;
		private double sizeY = 24;

		/** サイズを設定 */
		public EditNodeBuilder setSize(double x, double y) {
			sizeX = x;
			sizeY = y;
			return this;
		}

		/** ノード作成処理 */
		public Node build() {
			if (Type == EditNodeType.Number || Type == EditNodeType.Text) {
				// テキストセット
				AnchorPane root = new AnchorPane();
				TextField text = new TextField();
				Label label = new Label(LocalizeHandler.getLocalizedName(Data, Field) + ":");
				double textFieldX = 100;

				if (Type == EditNodeType.Text) {
					for (ChangeListener<?> listener : Listener) {
						text.textProperty().addListener((ChangeListener<? super String>) listener);
					}
					text.textProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(ObservableValue<? extends String> observable, String oldValue,
								String newValue) {
							RootController.INSTANCE.gunList.refresh();
							EditHelper.setData(Data, Field, text.getText());
							System.out.println("TextField txt Changed (newValue: " + newValue + ")");
						}
					});

				} else {

				}
				root.resize(sizeX, sizeY);

				text.setText(EditHelper.getData(Data, Field).toString());
				text.setFont(Font.font(10));
				text.setLayoutX(sizeX - textFieldX);
				text.setPrefSize(textFieldX, sizeY);

				label.setPrefSize(sizeX - textFieldX - 2, sizeY);
				label.setAlignment(Pos.CENTER_RIGHT);

				root.getChildren().add(text);
				root.getChildren().add(label);
				return root;
			}
			return null;
		}

	}
}