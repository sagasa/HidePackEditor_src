package model;

import java.util.Map;

import types.base.DataBase;

public class HideModel extends DataBase{
	// エディタサイドのみ
	transient public float[] vertArray;
	transient public float[] texArray;
	transient public Map<String, int[]> modelParts;

	// 共通
	public String texture;
	public Bone rootBone;

	public HideModel(float[] vert, float[] tex, Map<String, int[]> faces) {
		vertArray = vert;
		texArray = tex;
		modelParts = faces;
		rootBone = new Bone(faces.keySet());
	}
}
