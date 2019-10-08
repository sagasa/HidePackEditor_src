package editer;

import java.util.List;
import java.util.Random;

import io.PackCash;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import resources.HideImage;
import resources.Model;
import resources.Sound;
import types.PackInfo;
import types.base.DataBase;
import types.items.GunData;
import types.items.MagazineData;
import types.projectile.BulletData;

/** パック本体 */
public class HidePack {

	/** 未設定時のイメージ */
	public static final Image NullImage = new Image("/icon/notSet.png");

	/** 銃のList GunData */
	public static ObservableList<GunData> GunList;
	/** 弾のList MagazineData */
	public static ObservableList<MagazineData> MagazineList;
	/** 弾のList BulletData */
	public static ObservableList<BulletData> BulletList;
	/** IconのList Image */
	public static ObservableList<HideImage> IconList;
	/** ScopeのList Image */
	public static ObservableList<HideImage> ScopeList;
	/** SoundのList Sound */
	public static ObservableList<Sound> SoundList;
	/** TextureのList Image */
	public static ObservableList<HideImage> TextureList;
	/** ModelのList Model */
	public static ObservableList<Model> ModelList;
	/** 編集中のパック デフォルトを含む */
	public static ObservableList<PackInfo> OpenPacks;
	/** デフォルトパック */
	public static PackInfo DefaultPack;
	/** 初回かどうか */
	public static boolean isNewPack = true;

	public static Random random = new Random();

	/** 追加時にinit()を呼ばせる */

	static {
		clear();
	}

	/** パック初期化 */
	public static void clear() {
		GunList = FXCollections.observableArrayList();
		MagazineList = FXCollections.observableArrayList();
		BulletList = FXCollections.observableArrayList();
		IconList = FXCollections.observableArrayList();
		ScopeList = FXCollections.observableArrayList();
		SoundList = FXCollections.observableArrayList();
		TextureList = FXCollections.observableArrayList();
		OpenPacks = FXCollections.observableArrayList();
		DefaultPack = new PackInfo();
		DefaultPack.PACK_NAME = "default";
		DefaultPack.PACK_ROOTNAME = "default";
		DefaultPack.PACK_VER = "";
		DefaultPack.PackColor = Color.GRAY;
		OpenPacks.add(DefaultPack);
		// DataBaseを追加時にinitを呼ぶリスナー
		ListChangeListener<DataBase> propertyInit = change -> {
			while (change.next()) {
				change.getAddedSubList().forEach(data -> data.init());
			}
		};

		GunList.addListener(propertyInit);
		MagazineList.addListener(propertyInit);
		OpenPacks.addListener(propertyInit);
	}

	/** PackCashインポート */
	public static void addPack(PackCash pack) {
		// 放り込む
		GunList.addAll(pack.GunList);
		MagazineList.addAll(pack.MagazineList);
		IconList.addAll(pack.IconList);
		ScopeList.addAll(pack.ScopeList);
		SoundList.addAll(pack.SoundList);
		if (pack.Pack != null)
			OpenPacks.add(pack.Pack);
	}

	public static PackInfo addPack(PackInfo pack) {
		if (getPack(pack.PACK_NAME) == null || getPack(pack.PACK_NAME).isReference != pack.isReference) {
			OpenPacks.add(pack);
			return pack;
		} else {
			return getPack(pack.PACK_NAME);
		}
	}

	/** リストから一致する名前を取得 複数ある場合も1つしか戻らない */
	public static <T extends DataEntityInterface> T getDataByName(List<T> list, String displayName) {
		for (T data : list) {
			if (data.getDisplayName().equals(displayName)) {
				return data;
			}
		}
		return null;
	}

	/** 銃取得 */
	public static GunData getGunData(String displayName) {
		for (GunData data : GunList) {
			if (data.ITEM_DISPLAYNAME.equals(displayName)) {
				return data;
			}
		}
		return null;
	}

	/** 弾取得 */
	public static MagazineData getMagazineData(String displayName) {
		for (MagazineData data : MagazineList) {
			if (data.ITEM_DISPLAYNAME.equals(displayName)) {
				return data;
			}
		}
		return null;
	}

	/** パック取得 */
	public static PackInfo getPack(String packName) {
		for (PackInfo data : OpenPacks) {
			if (data.PACK_NAME.equals(packName)) {
				return data;
			}
		}
		return null;
	}


	/** アイコン取得 */
	public static HideImage getIcon(String string) {
		for (HideImage data : IconList) {
			if (data.getDisplayName().equals(string)) {
				return data;
			}
		}
		return null;
	}
	/** テクスチャ取得 */
	public static HideImage getTexture(String string) {
		for (HideImage data : TextureList) {
			if (data.getDisplayName().equals(string)) {
				return data;
			}
		}
		return null;
	}
}
