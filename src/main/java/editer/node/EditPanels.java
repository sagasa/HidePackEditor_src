package editer.node;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Set;
import java.util.function.BiConsumer;

import com.google.common.collect.ImmutableSet;

import editer.HidePack;
import editer.clip.ClipManager;
import editer.controller.RootController;
import helper.EditHelper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import localize.LocalizeHandler;
import resources.HideImage;
import types.PackInfo;
import types.base.DataBase;
import types.base.DataBase.DataEntry;
import types.base.DataPath;
import types.base.NamedData;
import types.editor.DataView;
import types.effect.Sound;
import types.gun.GunFireMode;
import types.gun.ProjectileData;
import types.items.GunData;
import types.items.ItemData;
import types.items.MagazineData;

/** 編集パネルのルート */
public class EditPanels extends Pane {

	/** 表示位置は別 */
	public final CurveEditPane curveEditPane = new CurveEditPane();

	/** 編集パネルの対象 */
	public enum EditType {
		NamedData(NamedData.class), Item(ItemData.class), Projectile(ProjectileData.class),
		Gun(GunData.class, (cons, data) -> cons.accept(Projectile, data.get(GunData.Data, null))),
		Magazine(MagazineData.class, (cons, data) -> cons.accept(Projectile, data.get(MagazineData.Data, null))),
		Pack(PackInfo.class);

		/** 判別用の型 */
		public final Class<? extends DataBase> Clazz;
		private BiConsumer<BiConsumer<EditType, DataBase>, DataBase> AddFunc;

		private EditType(Class<? extends DataBase> clazz) {
			this(clazz, null);
		}

		@SuppressWarnings("unchecked")
		private <T extends DataBase> EditType(Class<T> clazz, BiConsumer<BiConsumer<EditType, DataBase>, T> addFunc) {
			Clazz = clazz;
			AddFunc = (BiConsumer<BiConsumer<EditType, DataBase>, DataBase>) addFunc;
		}

		public boolean isType(DataBase data) {
			return Clazz.isAssignableFrom(data.getClass());
		}

		/** 型からエディタを選択 */
		public static Set<EditType> getType(DataBase data) {
			if (data != null) {
				return ImmutableSet.copyOf(Arrays.stream(values()).filter(type -> type.isType(data)).iterator());
			}
			return null;
		}

		public static void editValue(DataBase data, BiConsumer<EditType, DataBase> func) {
			getType(data).forEach(type -> {
				System.out.println("add edit " + type);
				if (type.AddFunc != null)
					type.AddFunc.accept(func, data);
				func.accept(type, data);
			});
		}
	}

	private FlowPane rootPane;

	/** 各編集パネルの表示状態管理用プロパティ */
	private EnumMap<EditType, ObjectProperty<? extends DataBase>> editModes = new EnumMap<>(EditType.class);

	public EditPanels() {
		rootPane = new FlowPane();
		rootPane.setVgap(5);
		rootPane.setHgap(5);
		rootPane.setOrientation(Orientation.VERTICAL);
		rootPane.prefWrapLengthProperty().bind(this.heightProperty());
		rootPane.maxHeightProperty().bind(this.heightProperty());
		this.getChildren().add(rootPane);

		for (EditType type : EditType.values()) {
			editModes.put(type, new SimpleObjectProperty<>());
		}

		// ItemName
		writeItemInfoEditor();
		writeGunEditer();
		writeMagazineEditor();
		writeProjectileEditor();
		writePackInfoEditor();
		// writePackEditer();
		// writeModelEditer();
	}

	/** MagazineData */
	private void writeProjectileEditor() {
		final EditType type = EditType.Projectile;
		// cate
		addEditPane(makeCateEditPanel(type, ProjectileData.GunInfo), type);
		addEditPane(makeCateEditPanel(type, ProjectileData.BulletInfo), type);
		addEditPane(makeCateEditPanel(type, ProjectileData.DamageInfo), type);
		addEditPane(makeCateEditPanel(type, ProjectileData.ExplosionInfo), type);// TODO
		addEditPane(makeCateEditPanel(type, ProjectileData.KnockBackInfo), type);
		// sound
		TabPane sound = new TabPane();
		sound.setMaxWidth(300);
		sound.getTabs().addAll(makeTab("Shoot", makeSoundEditer(type, DataPath.of(ProjectileData.SoundShoot))),
				makeTab("Reload", makeSoundEditer(type, DataPath.of(ProjectileData.SoundReload))),
				makeTab("HitEntity", makeSoundEditer(type, DataPath.of(ProjectileData.SoundHitEntity))),
				makeTab("Hit", makeSoundEditer(type, DataPath.of(ProjectileData.SoundHit))),
				makeTab("Pass", makeSoundEditer(type, DataPath.of(ProjectileData.SoundPassing))));
		addEditPane(sound, type);
		// recoil
		// recoil
		TabPane recoil = new TabPane();
		recoil.setMaxWidth(300);
		recoil.getTabs().addAll(makeTab("Default", makeRecoilEditer(type, DataPath.of(ProjectileData.Recoil))),
				makeTab("ADS", makeRecoilEditer(type, DataPath.of(ProjectileData.RecoilADS))),
				makeTab("Sneak", makeRecoilEditer(type, DataPath.of(ProjectileData.RecoilSneak))));
		addEditPane(recoil, type);
	}

	/** GunEditer */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void writeGunEditer() {
		final EditType type = EditType.Gun;
		DataView<GunData> view = new DataView<>(GunData.class, 1);
		view.setValue(0, (ObservableObjectValue) editModes.get(type));
		ObservableObjectValue<GunData> editValue = (ObservableObjectValue<GunData>) editModes.get(type);

		Pane iteminfo = makeCateEditPanel(type, GunData.ItemInfo);
		// 親
		Node parentname = EditNode
				.editString(editValue, EditType.NamedData, DataPath.of(NamedData.ParentName), HidePack.GunList)
				.setChangeListner((ov, nv) -> {
					if (editValue.get() != null)
						((NamedData) editValue.get()).onChangeParentName((String) ov, (String) nv);
				});
		iteminfo.getChildren().add(parentname);
		addEditPane(iteminfo, type);
		addEditPane(makeCateEditPanel(type, GunData.GunInfo), type);
		// useBullet
		addEditPane(new ListEditNode<>(editValue, type, DataPath.of(GunData.UseMagazine), HidePack.MagazineList,
				(data -> data.getSystemName())), type);
		// fireMode
		addEditPane(new ListEditNode<>(editValue, type, DataPath.of(GunData.FireMode), GunFireMode.getList()), type);

		// scope
		Pane scope = makeImageNode(type, DataPath.of(GunData.ScopeName), HidePack.ScopeList, view);
		scope.getChildren().add(makeCateEditPanel(EditType.Gun, GunData.ScopeInfo));
		addEditPane(scope, type);

		addEditPane(new RecoilGraphNode(editValue), type);
		// */
	}

	@SuppressWarnings("unchecked")
	/** MagazineData */
	private void writeMagazineEditor() {
		final EditType type = EditType.Magazine;

		final ObservableObjectValue<MagazineData> editValue = (ObservableObjectValue<MagazineData>) editModes.get(type);
		// icon
		// addEditPane(makeImageNode(type, DataPath.of(ItemData.IconName),
		// HidePack.IconList), type);
		// Cate0
		Pane root = makeCateEditPanel(type, 0);
		root.getChildren().add(EditNode.editNumber(editValue, EditType.Item, DataPath.of(ItemData.StackSize)));
		// 親
		Node parentname = EditNode
				.editString(editValue, EditType.NamedData, DataPath.of(NamedData.ParentName), HidePack.MagazineList)
				.setChangeListner((ov, nv) -> {
					if (editValue.get() != null)
						((NamedData) editValue.get()).onChangeParentName((String) ov, (String) nv);
				});
		root.getChildren().add(parentname);
		addEditPane(root, type);
	}

	/** ItemData用名称+アイコン編集ノード */
	@SuppressWarnings("unchecked")
	private void writeItemInfoEditor() {
		final EditType type = EditType.Item;
		DataView<ItemData> view = new DataView<>(ItemData.class, 1);
		view.setValue(0, (ObservableObjectValue<ItemData>) editModes.get(type));
		ObservableObjectValue<? extends DataBase> editValue = editModes.get(type);
		VBox root = new VBox();

		// 短縮名
		Node shortname = EditNode.editString(editValue, type, DataPath.of(ItemData.ShortName))
				.setChangeListner((ov, nv) -> {
					if (editValue.get() != null)
						((NamedData) editValue.get()).onChangeSystemName((String) ov, (String) nv);
				});
		// 表示名
		Node dizplayname = EditNode.editString(editValue, type, DataPath.of(ItemData.DisplayName))
				.setChangeListner((ov, nv) -> RootController.refreshList());

		// icon
		Node icon = makeImageNode(type, DataPath.of(ItemData.IconName), HidePack.IconList, view);
		// model
		Node model = EditNode.editString(editValue, type, DataPath.of(ItemData.ModelName), HidePack.IconList);

		root.getChildren().addAll(dizplayname, shortname, icon, model);
		// root.setPrefSize(200, 72);
		addEditPane(root, type);
	}

	private void writePackInfoEditor() {
		final EditType type = EditType.Pack;
		ObservableObjectValue<? extends DataBase> editValue = editModes.get(type);
		addEditPane(makeCateEditPanel(type, 0), type);
	}

	/**
	 * エディタの内容
	 *
	 * @param <T>
	 */
	@SuppressWarnings("unchecked")
	private <T> void startEdit(EditType type, T data) {
		// System.out.println("edit ");
		((ObjectProperty<T>) editModes.get(type)).set(data);
	}

	/** 編集中のデータ */
	private DataBase current;

	/** 表示中のものなら表示解除 */
	public void removeEditValue(DataBase data) {
		if (current != null && current.equals(data))
			setEditValue(null);
	}

	/** エディタの内容設定 */
	public void setEditValue(DataBase data) {
		current = data;
		for (ObjectProperty<? extends DataBase> prop : editModes.values())
			prop.set(null);
		if (data != null)
			EditType.editValue(data, this::startEdit);
	}

	private void addEditPane(Node node, EditType type) {
		node.setStyle("-fx-background-color: lightGray;");
		node.managedProperty().bind(editModes.get(type).isNotNull());
		node.visibleProperty().bind(editModes.get(type).isNotNull());
		rootPane.getChildren().add(node);
	}

	private static ClipManager clipManager = new ClipManager();

	static Region makeClipUI(ObservableObjectValue<? extends DataBase> value, EditType type, DataPath path) {
		ImageView clipDelete = new ImageView("/icon/clipDelete.png");
		ImageView clipAdd = new ImageView("/icon/clipAdd.png");
		ImageView clipRemove = new ImageView("/icon/clipRemove.png");
		ImageView clipPaste = new ImageView("/icon/clipPut.png");

		Label addremove = new Label();
		addremove.setPrefSize(16, 24);
		addremove.setAlignment(Pos.CENTER);
		addremove.setGraphic(clipAdd);
		addremove.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				if (clipManager.hasPath(path))
					clipManager.remove(path);
				else
					clipManager.add(value.get(), path);

				addremove.setGraphic(clipManager.hasPath(path) ? clipRemove : clipAdd);
			}
		});
		addremove.visibleProperty().bind(clipManager.type.isEqualTo(type.Clazz).or(clipManager.type.isNull()));

		Label clearpaste = new Label();
		clearpaste.setPrefSize(16, 24);
		clearpaste.setAlignment(Pos.CENTER);
		clearpaste.setGraphic(clipDelete);
		clearpaste.setTranslateX(16);
		clearpaste.setOnMouseClicked(e -> {
			if (e.getButton() == MouseButton.PRIMARY) {
				clearpaste.setGraphic(clipManager.hasPath(path) ? clipPaste : clipDelete);
			}
		});

		Class<?> clazz = EditHelper.getType(type.Clazz, path);
		clipManager.scope.addListener((v, ov, nv) -> {
			if (clipManager.hasPath(path)) {
				clipManager.paste(value.get(), path);
			} else {

			}
		});
		clearpaste.visibleProperty().bind(clipManager.scope.isNotNull());

		Pane res = new Pane();
		res.getChildren().addAll(addremove, clearpaste);
		res.setPrefSize(32, 24);

		return res;
	}

	private static Region setSize(Region node, ObservableValue<? extends Number> x, double y) {
		node.prefWidthProperty().bind(x);
		node.setPrefHeight(y);
		return node;
	}

	private Tab makeTab(String name, Node node) {
		Tab res = new Tab(name, node);
		res.setClosable(false);
		return res;
	}

	/** Soundの詳細 */
	private Region makeSoundEditer(EditType type, DataPath path) {
		VBox root = new VBox();

		ObservableObjectValue<? extends DataBase> editValue = editModes.get(type);
		Label label = new Label(LocalizeHandler.getLocalizedName(type.Clazz, path));
		label.setPrefWidth(200);
		label.setAlignment(Pos.CENTER);
		root.getChildren().add(label);
		// 数値系
		root.getChildren().add(EditNode.editString(editValue, type, path.append(Sound.Name), HidePack.SoundList));
		root.getChildren().add(makeCateEditPanel(type, -1, path));
		return root;
	}

	private Region makeRecoilEditer(EditType type, DataPath path) {
		VBox root = new VBox();
		// 数値系
		Pane pane = makeCateEditPanel(type, -1, path);

		// バインドチェック
		/*
		 * boolean value = recoil.USE; EditHelper.getProperty(recoil, "USE",
		 * boolean.class).setValue(!value); EditHelper.getProperty(recoil, "USE",
		 * boolean.class).setValue(value); //
		 */

		root.getChildren().addAll(pane);
		return root;
	}

	/** StringでListからImageを選択し表示するパネル設定パネル */
	private Pane makeImageNode(EditType type, DataPath dataPath, ObservableList<HideImage> list, DataView<?> view) {
		VBox root = new VBox();
		ObservableObjectValue<? extends DataBase> editValue = editModes.get(type);
		HideImageView iconview = new HideImageView(view, dataPath, list);
		iconview.setFitWidth(64);
		iconview.setFitHeight(64);
		VBox.setMargin(iconview, new Insets(5, 0, 0, 5));
		iconview.setPreserveRatio(true);
		Node name = EditNode.editString(editValue, type, dataPath, list);
		root.getChildren().addAll(iconview, name);
		return root;
	}

	/** カテゴリが付いた値を編集するノード */
	private Pane makeCateEditPanel(EditType type, int cate) {
		return makeCateEditPanel(type, cate, null);
	}

	/** カテゴリが付いた値を編集するノード */
	private Pane makeCateEditPanel(EditType type, int cate, DataPath path) {
		ObservableObjectValue<? extends DataBase> editValue = editModes.get(type);
		VBox root = new VBox();
		Class<? extends DataBase> clazz = type.Clazz;
		// pathがあるなら指定されたクラスで実行
		for (DataEntry<?> data : EditHelper.getDataEntries(type.Clazz, path)) {
			// pathが無いなら作る
			DataPath fieldPath = path == null ? DataPath.of(data) : path.append(data);
			int c = EditHelper.getCate(clazz, fieldPath);
			if (c == cate) {
				if (EditHelper.isString(clazz, fieldPath)) {
					root.getChildren().add(EditNode.editString(editValue, type, fieldPath));
				} else if (EditHelper.isBoolean(clazz, fieldPath)) {
					root.getChildren().add(EditNode.editBoolean(editValue, type, fieldPath));
				} else if (EditHelper.isNumber(clazz, fieldPath)) {
					root.getChildren().add(EditNode.editNumber(editValue, type, fieldPath));
				} else if (EditHelper.isCurve(clazz, fieldPath)) {
					root.getChildren().add(EditNode.editCurve(editValue, type, fieldPath, curveEditPane));
				}
			}
		}
		return root;
	}
	// ベースサイズは200x24

	/** HideModel */
//	private void writeModelEditer() {
//		final EditType type = EditType.Model;
//		addEditPane(makePos3Editer(type, new DataPath("offsetFirstPerson")), type);
//		// 右側にモデル確認ビューを置く
//		ModelView modelView = new ModelView(editValue);
//		modelView.translateXProperty().bind(rootPane.widthProperty().add(5));
//		modelView.prefWidthProperty().bind(widthProperty().subtract(rootPane.widthProperty()).add(-5));
//		modelView.prefHeightProperty().bind(heightProperty());
//
//		modelView.managedProperty().bind(editModes.get(type));
//		modelView.visibleProperty().bind(editModes.get(type));
//
//		ModelTreeView treeView = new ModelTreeView(editValue, modelView);
//		addEditPane(treeView, type);
//		// MSEditer
//		VBox msEditer = new VBox();
//		Label label = new Label("ModelSelecter");
//		label.setPrefWidth(200);
//		label.setAlignment(Pos.CENTER);
//
//		EditNode defaultModel = new EditNode(treeView.currentItem, EditType.ModelSelector, new DataPath("defaultModel"),
//				EditNodeType.StringFromList);
//
//		msEditer.getChildren().addAll(label, defaultModel);
//
//		msEditer.setStyle("-fx-background-color: lightGray;");
//		msEditer.managedProperty().bind(editModes.get(type).and(treeView.currentItemIsBone.not()));
//		msEditer.visibleProperty().bind(editModes.get(type).and(treeView.currentItemIsBone.not()));
//
//		// BoneEditer
//		VBox boneEditer = new VBox();
//		label = new Label("Bone");
//		label.setPrefWidth(200);
//		label.setAlignment(Pos.CENTER);
//
//		AnimationListNode animationEditer = new AnimationListNode(treeView.currentItem, new DataPath("animation"));
//
//		animationEditer.setPrefSize(160, 300);
//
//		boneEditer.getChildren().addAll(label, animationEditer);
//
//		boneEditer.setStyle("-fx-background-color: lightGray;");
//		boneEditer.managedProperty().bind(editModes.get(type).and(treeView.currentItemIsBone));
//		boneEditer.visibleProperty().bind(editModes.get(type).and(treeView.currentItemIsBone));
//
//		rootPane.getChildren().addAll(msEditer, boneEditer);
//
//		this.getChildren().addAll(modelView);
//	}

//	private void addBulletEditer(Pane editer) {
//		final EditType type = EditType.Magazine;
//		final DataPath top = new DataPath("BULLETDATA");
//		// Cate0
//		addEditPane(makeCateEditPanel(type, 0, top), type);
//		// Cate1
//		addEditPane(makeCateEditPanel(type, 1, top), type);
//		// Cate2
//		addEditPane(makeCateEditPanel(type, 2, top), type);
//		// Cate3
//		addEditPane(makeCateEditPanel(type, 3, top), type);
//
//	}

//	private Region makePos3Editer(EditType type, DataPath path) {
//		return makePos3Editer(editValue, type, path);
//	}
//
//	/** Pos3f */
//	public static Region makePos3Editer(ObjectProperty<DataBase<?>> value, EditType type, DataPath path) {
//		VBox root = new VBox();
//		root.setPrefWidth(200);
//		Label label = new Label(EditHelper.getLocalizedName(type.Clazz, path));
//		label.prefWidthProperty().bind(root.widthProperty().subtract(4));
//		label.setAlignment(Pos.CENTER);
//		root.getChildren().add(label);
//		// 数値系
//		HBox hbox = new HBox();
//		ObservableValue<Number> width = root.widthProperty().subtract(6).divide(3);
//		hbox.getChildren().add(setSize(new EditNode(value, type, path.append("X"), EditNodeType.Number), width, 24));
//		hbox.getChildren().add(setSize(new EditNode(value, type, path.append("Y"), EditNodeType.Number), width, 24));
//		hbox.getChildren().add(setSize(new EditNode(value, type, path.append("Z"), EditNodeType.Number), width, 24));
//		root.getChildren().add(hbox);
//		return root;
//	}

}
