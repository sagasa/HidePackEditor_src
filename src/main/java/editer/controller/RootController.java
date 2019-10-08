package editer.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import editer.DataEntityInterface;
import editer.HidePack;
import editer.node.EditPanels;
import editer.node.ModelView;
import helper.ArrayEditer;
import helper.DataPath;
import helper.EditHelper;
import io.ModelIO;
import io.PackCash;
import io.PackIO;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import types.PackInfo;
import types.items.GunData;
import types.items.MagazineData;

public class RootController implements Initializable {
	private static final Logger log = LogManager.getLogger();
	public static RootController INSTANCE;
	public static Stage STAGE;

	public EditPanels editer;

	public TextField packSearch;
	public ListView<DataEntityInterface> packList;

	public TextField itemSearch;
	public TabPane itemTab;
	public ListView<DataEntityInterface> gunList;
	public ListView<DataEntityInterface> magazineList;
	public ListView<DataEntityInterface> soundList;
	public ListView<DataEntityInterface> iconList;
	public ListView<DataEntityInterface> modelList;
	public ListView<DataEntityInterface> modelInfoList;

	/** クリップエディタ */
	private ClipController clipController;
	private Stage clipEditer;

	/** writeのリスナー */
	private ListChangeListener<DataEntityInterface> writeListener = change -> {
		write();
		while (change.next()) {
			change.getRemoved().forEach(remove -> cancelEdit(remove));
		}
	};

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		INSTANCE = this;
		packList.setCellFactory(
				ColordListCell.getCellFactory(HidePack.OpenPacks, data -> !HidePack.DefaultPack.equals(data)));
		gunList.setCellFactory(ColordListCell.getCellFactory(HidePack.GunList));
		magazineList.setCellFactory(ColordListCell.getCellFactory(HidePack.MagazineList));
		soundList.setCellFactory(ColordListCell.getCellFactory(HidePack.SoundList));
		iconList.setCellFactory(ColordListCell.getCellFactory(HidePack.IconList));
		// modelList.setCellFactory(ColordListCell.getCellFactory(HidePack.));TODO
		// Serch用フック
		itemSearch.textProperty().addListener(change -> write());
		packSearch.textProperty().addListener(change -> write());
		// リスト通知用フック
		HidePack.GunList.addListener(new WeakListChangeListener<>(writeListener));
		HidePack.MagazineList.addListener(new WeakListChangeListener<>(writeListener));
		HidePack.IconList.addListener(new WeakListChangeListener<>(writeListener));
		HidePack.ScopeList.addListener(new WeakListChangeListener<>(writeListener));
		HidePack.SoundList.addListener(new WeakListChangeListener<>(writeListener));
		HidePack.OpenPacks.addListener(new WeakListChangeListener<>(writeListener));

		bindEditer(packList, (item) -> editPack(item));
		bindEditer(gunList, (item) -> editGun(item));
		bindEditer(magazineList, (item) -> editMagazine(item));
		bindEditer(modelInfoList, (item) -> editModelInfo(item));

		itemTab.getSelectionModel().selectedItemProperty().addListener((v, n, o) -> itemTabChange());


		// TODO
		//*
		Pane modelV = new Pane();
		Stage modelView = new Stage(StageStyle.UTILITY);
		ModelView mv = new ModelView(modelV);
		mv.showModelView(ModelIO.read());
		modelView = new Stage(StageStyle.UTILITY);
		modelView.setScene(new Scene(modelV));
		modelView.initOwner(STAGE);
		modelView.initModality(Modality.NONE);
		// clipEditer.setResizable(false);
		modelView.setTitle("ModelView");
		modelView.show();//*/

		write();

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/clip.fxml"));
		try {
			System.out.println(loader.load().toString());
			;
		} catch (IOException e) {
			e.printStackTrace();
		}
		Parent root = loader.getRoot();
		clipController = loader.getController();
		Scene scene = new Scene(root);
		clipEditer = new Stage(StageStyle.UTILITY);
		clipEditer.setScene(scene);
		clipEditer.initOwner(STAGE);
		clipEditer.initModality(Modality.NONE);
		// clipEditer.setResizable(false);
		clipEditer.setTitle("ClipBord");
		clipEditer.showingProperty().addListener((v, ov, nv) -> {
			if (!nv) {

			}
		});
	}

	private static void bindEditer(ListView<DataEntityInterface> list, Consumer<DataEntityInterface> run) {
		// フォーカスが切れたら選択解除
		list.focusedProperty().addListener((v, ov, nv) -> {
			if (!nv)
				list.getSelectionModel().clearSelection();
		});
		list.getSelectionModel().selectedItemProperty().addListener((v, ov, nv) -> {
			if (nv != null)
				run.accept(nv);
		});
	}

	/** パックから要素が削除されたとき編集中なら編集を中止する */
	private void cancelEdit(DataEntityInterface obj) {
		if (editer.getEditValue() == obj) {
			editer.setEditValue(null);
		}
	}

	/** リストをリフレッシュ */
	public static void refreshList() {
		INSTANCE.refresh();
	}

	/** リストをリフレッシュ */
	public void refresh() {
		packList.refresh();
		gunList.refresh();
		magazineList.refresh();
		soundList.refresh();
		iconList.refresh();
		modelList.refresh();
	}

	/** CurrentPackの内容をリストに反映 */
	public static void writeList() {
		INSTANCE.write();
	}

	/** Packの内容をリストに反映 */
	public void write() {
		// Pack
		packList.setItems(
				FXCollections.observableArrayList(ArrayEditer.Search(HidePack.OpenPacks, packSearch.getText())));
		// Gun
		gunList.setItems(FXCollections.observableArrayList(ArrayEditer.Search(HidePack.GunList, itemSearch.getText())));
		// Magazine
		magazineList.setItems(
				FXCollections.observableArrayList(ArrayEditer.Search(HidePack.MagazineList, itemSearch.getText())));
		// Icon
		iconList.setItems(
				FXCollections.observableArrayList(ArrayEditer.Search(HidePack.IconList, itemSearch.getText())));
		// Sound
		soundList.setItems(
				FXCollections.observableArrayList(ArrayEditer.Search(HidePack.SoundList, itemSearch.getText())));
	}

	/** タブ切り替え時にアイテムエディタを描画 */
	public void itemTabChange() {
		int id = itemTab.getSelectionModel().getSelectedIndex();
		if (id != 0) {
			gunList.getSelectionModel().clearSelection();
		}
		if (id != 1) {
			magazineList.getSelectionModel().clearSelection();
		}
	}

	@FXML
	public void isGun(DragEvent e) {
		System.out.println("drag");
		// ドラッグボードを取得
		Dragboard board = e.getDragboard();
		for (File file : board.getFiles()) {
			System.out.println(file.getName());
		}
		e.acceptTransferModes(TransferMode.COPY);
		e.consume();
	}

	public void newPack() {

	}

	public void openNewPack() {

	}


	// ========メニュー操作========
	public void openPack() {
		FileChooser fxtest = new FileChooser();
		fxtest.setInitialDirectory(new File("./"));
		fxtest.getExtensionFilters().add(new ExtensionFilter("zip", "*.zip"));
		File file = fxtest.showOpenDialog(STAGE);
		if (file != null) {
			PackCash pack = PackIO.readPack(file);
			// パックが1つなら
			if (HidePack.isNewPack) {
				HidePack.isNewPack = false;
				pack.setPack(false);
				HidePack.addPack(pack);
				write();
			} else {
				// インポートダイアログを開く
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/import.fxml"));
				try {
					loader.load();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Parent root = loader.getRoot();
				ImportController controller = loader.getController();
				controller.setPack(pack);
				Scene scene = new Scene(root);
				Stage confirmDialog = new Stage(StageStyle.UTILITY);
				confirmDialog.setScene(scene);
				confirmDialog.initOwner(STAGE);
				confirmDialog.initModality(Modality.WINDOW_MODAL);
				confirmDialog.setResizable(false);
				confirmDialog.setTitle("Select an Option");
				confirmDialog.show();
			}
		}
	}

	public void save() {
		PackIO.export();
	}

	public void saveas() {
		PackIO.export();
	}

	// FXCollections.observableArrayList(HidePack.OpenPacks.stream().filter(data ->
	// Search(data.getDisplayName(),
	// packSearch.getText())).sorted().collect(Collectors.toList()))
	// ========編集========

	public void editClear() {
		editer.setEditValue(null);
	}

	public void editPack(DataEntityInterface item) {
		if (item != null) {
			log.debug(HidePack.getPack(item.getDisplayName()).toString());
			editer.setEditValue(HidePack.getPack(item.getDisplayName()));
		}
	}

	public void editGun(DataEntityInterface item) {
		if (item != null) {
			log.debug(HidePack.getGunData(item.getDisplayName()).toString() + HidePack.GunList);
			editer.setEditValue(HidePack.getGunData(item.getDisplayName()));
		}
	}

	public void editMagazine(DataEntityInterface item) {
		if (item != null) {
			log.debug(HidePack.getMagazineData(item.getDisplayName()).toString());
			editer.setEditValue(HidePack.getMagazineData(item.getDisplayName()));
		}
	}

	public void editModelInfo(DataEntityInterface item) {
		if (item != null) {
			log.debug(HidePack.getMagazineData(item.getDisplayName()).toString());
			editer.setEditValue(HidePack.getMagazineData(item.getDisplayName()));
		}
	}

	@FXML
	public void importGun(DragEvent event) {
		System.out.println(packList.getFocusModel().getFocusedItem());
	}

	// ===========追加系============
	private static int packNamePointer = 0;
	private static int gunNamePointer = 0;
	private static int bulletNamePointer = 0;

	public void addPack() {
		log.debug("addPack");
		PackInfo pack = new PackInfo();
		if (HidePack.getPack("New Pack No." + packNamePointer) == null) {
			packNamePointer++;
		}
		while (HidePack.getPack("New Pack No." + packNamePointer) != null) {
			packNamePointer++;
		}
		pack.PACK_NAME = "New Pack No." + packNamePointer;
		HidePack.OpenPacks.add(pack);
		write();
	}

	public void addGun() {
		// clipEditer.show();//TODO
		log.debug("addGun");
		GunData newGun = new GunData();
		if (HidePack.getGunData("New Gun No." + gunNamePointer) == null) {
			gunNamePointer++;
		}
		while (HidePack.getGunData("New Gun No." + gunNamePointer) != null) {
			gunNamePointer++;
		}
		newGun.ITEM_SHORTNAME = "gun_" + gunNamePointer;
		newGun.ITEM_DISPLAYNAME = "New Gun No." + gunNamePointer;
		newGun.RootPack.set(HidePack.DefaultPack);
		HidePack.GunList.add(newGun);
		write();
	}

	public void addMagazine() {
		log.debug("addBullet");
		MagazineData magazine = new MagazineData();
		if (HidePack.getGunData("New Magazine No." + bulletNamePointer) == null) {
			bulletNamePointer++;
		}
		while (HidePack.getGunData("New Magazine No." + bulletNamePointer) != null) {
			bulletNamePointer++;
		}
		magazine.ITEM_SHORTNAME = "magazine_" + bulletNamePointer;
		magazine.ITEM_DISPLAYNAME = "New Magazine No." + bulletNamePointer;
		magazine.RootPack.set(HidePack.DefaultPack);
		HidePack.MagazineList.add(magazine);
		write();
	}

	public void importIcon() {
		PackIO.importIcon();
	}

	public void importSound() {
		PackIO.importSound();
	}

	// ===========リストセル============
	/** カラーアイコン付きのリストシェル */
	public static class ColordListCell extends ListCell<DataEntityInterface> {

		public static Callback<ListView<DataEntityInterface>, ListCell<DataEntityInterface>> getCellFactory(
				ObservableList<? extends DataEntityInterface> fromList) {
			return getCellFactory(fromList, null);
		}

		/**
		 * ファクトリー
		 *
		 * @param fromList  削除元になるリスト Null許容
		 * @param candelete 削除ボタンの表示判定 Null許容
		 */
		public static Callback<ListView<DataEntityInterface>, ListCell<DataEntityInterface>> getCellFactory(
				ObservableList<? extends DataEntityInterface> fromList,
				Function<DataEntityInterface, Boolean> candelete) {
			return new Callback<ListView<DataEntityInterface>, ListCell<DataEntityInterface>>() {
				@Override
				public ListCell<DataEntityInterface> call(ListView<DataEntityInterface> arg0) {
					return new ColordListCell(fromList, candelete);
				}
			};
		}

		/** 削除ボタンを出すかどうかの判定 */
		private Function<DataEntityInterface, Boolean> canDelete = null;
		private Label delete = new Label();
		private Rectangle color = new Rectangle(20, 20);
		private Label text = new Label();
		private AnchorPane root = new AnchorPane();
		private boolean isBind = false;

		public ColordListCell(ObservableList<? extends DataEntityInterface> fromList,
				Function<DataEntityInterface, Boolean> candelete) {

			root.getChildren().addAll(color, delete, text);
			// 削除元がない場合は無視
			delete.setDisable(fromList == null);
			if (fromList != null) {
				canDelete = candelete;
				delete.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
					fromList.remove(getItem());
				});
			}
			delete.setStyle("-fx-background-image : url('/icon/delete.png');");
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void updateItem(DataEntityInterface data, boolean empty) {
			super.updateItem(data, empty);
			// 初期化
			if (!isBind) {
				setMaxSize(40, 24);
				root.prefWidthProperty().bind(widthProperty().subtract(14));
				root.prefHeightProperty().bind(heightProperty().subtract(6));
				delete.prefWidthProperty().bind(root.heightProperty());
				delete.prefHeightProperty().bind(root.heightProperty());
				delete.translateXProperty().bind(root.widthProperty().subtract(root.heightProperty().multiply(1.1)));
				color.widthProperty().bind(root.heightProperty());
				color.heightProperty().bind(root.heightProperty());
				text.translateXProperty().bind(root.heightProperty().add(5));
				isBind = true;
			}
			if (!empty) {
				delete.setVisible(canDelete == null || canDelete.apply(data));
				text.setText(data.getDisplayName());
				if (data.getRootPack() != null) {
					color.fillProperty().bind((ObservableValue<? extends Paint>) EditHelper
							.getProperty(data.getRootPack().get(), new DataPath("PackColor")));
				}
				setStyle("-fx-border-color: orange; -fx-border-width: 0 4 0 4; -fx-padding: 3 3 1 3");
				setGraphic(root);
				getIndex();
				getListView().getItems().size();
			} else {
				setGraphic(null);
				setStyle(null);
			}
		}
	}
}
