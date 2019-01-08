package com.innovation.utils;


import java.util.ArrayList;
import java.util.List;

public class ConstUtils {
	/**		动物种类 : 猪	*/
	public static final int ANIMAL_TYPE_NONE	= 0;
	/**		动物种类 : 猪	*/
	public static final int ANIMAL_TYPE_PIG		= 1;
	/**		动物种类 : 牛	*/
	public static final int ANIMAL_TYPE_CATTLE	= 2;
	/**		动物种类 : 驴	*/
	public static final int ANIMAL_TYPE_DONKEY	= 3;


	/**		字符串:未知		*/
	private static final String UNKNOWN_RESULT = "未知";

	private static final String[] MOBILE_SSD_TF16 = {
			"占位用",
			"ssdmobilenetv1_pig_0919_tf16_192.pb",						//pig
			"ssdmobilenetv1_pig_0919_tf16_192.pb",		//cattle
			"ssdmobilenetv1_pig_0919_tf16_192.pb"		//donkey
	};

	/*	动物种类	*/
	private static final String[][]	ANIMAL_TYPE =	{
			{"1", "猪"},
			{"2", "牛"},
			{"3", "驴"}
	};

	/*	动物子种类	*/
	private static final String[][]	ANIMAL_SUB_TYPE =	{
			{"1", "101", "种猪"},
			{"1", "102", "育肥猪"},
			{"2", "1", "肉牛"},
			{"2", "2", "繁母牛"},
			{"3", "301", "驴"},
			{"3", "302", "肉驴"},
			{"3", "303", "能繁母驴"},
			{"3", "304", "种驴"}
	};

	/*	投保种类	*/
	public static final String[][]	INSURE_TYPE =	{
			{"1", "101", "种猪养殖保险"},
			{"1", "102", "育肥猪养殖保险"},
			{"2", "1", "肉牛养殖保险"},
			{"2", "2", "能繁母牛养殖保险"},
			{"3", "301", "驴养殖保险"},
			{"3", "302", "肉驴养殖保险"},
			{"3", "303", "能繁母驴养殖保险"},
			{"3", "304", "种驴养殖保险"}
	};


	/**
	 * 根据动物类型获取相应模型文件名
	 * */
	public static String getPBFile(int animalType) {
		return MOBILE_SSD_TF16[animalType];
	}

	private static List<String> transfer2dTo1d (String[][] transferFrom, int transferIndex) {
		if(transferFrom == null)	return null;
		List<String> result = new ArrayList<String>();
		for (String[] arr : transferFrom) {
			result.add(arr[transferIndex]);
		}
		return result;
	}

	/**
	 * 获取动物种类数组[[code, caption],...]
	 *
	 * */
	public static String[][]	getAnimalTypes() {
		return ANIMAL_TYPE;
	}

	/**
	 * 获取动物种类Code List<String>
	 * */
	public static List<String> getAnimalTypeCodes() {
		return transfer2dTo1d(ANIMAL_TYPE, 0);
	}

	/**
	 * 获取动物种类Code List<Integer>
	 * */
	public static List<Integer> getAnimalTypeIntCodes() {
		List<String> animalTypeCodes = transfer2dTo1d(ANIMAL_TYPE, 0);
		List<Integer> result = new ArrayList<Integer>();
		for (String code : animalTypeCodes) {
			result.add(toInt(code));
		}
		return result;
	}


	/**
	 * 获取动物种类名称List<String>
	 *
	 * */
	public static List<String> getAnimalTypeCaptions() {
		return transfer2dTo1d(ANIMAL_TYPE, 1);
	}


	/**
	 * 获取动物子种类List<String[code, caption]>
	 *
	 * */
	public static List<String[]>	getAnimalSubTypes(int animalType) {
		List<String[]> result = new ArrayList<String[]>();
		for(String[] arr : ANIMAL_SUB_TYPE) {
			if(toInt(arr[0]) == animalType) {
				result.add(new String[]{arr[1], arr[2]});
			}
		}
		return result;
	}


	/**
	 * 获取动物子种类Code List<String>
	 *
	 * */
	public static List<String> getAnimalSubTypeCodes(int animalType) {
		List<String> result = new ArrayList<String>();
		for(String[] arr : ANIMAL_SUB_TYPE) {
			if(toInt(arr[0]) == animalType) {
				// CODE
				result.add(arr[1]);
			}
		}
		return result;
	}

	/**
	 * 获取动物子种类Code List<Integer>
	 *
	 * */
	public static List<Integer> getAnimalSubTypeIntCodes(int animalType) {
		List<Integer> result = new ArrayList<Integer>();
		for(String[] arr : ANIMAL_SUB_TYPE) {
			if(toInt(arr[0]) == animalType) {
				// CODE
				result.add(toInt(arr[1]));
			}
		}
		return result;
	}

	/**
	 * 获取动物子种类名称 List<String>
	 *
	 * */
	public static List<String> getAnimalSubTypeCaptions(int animalType) {
		List<String> result = new ArrayList<String>();
		for(String[] arr : ANIMAL_SUB_TYPE) {
			if(toInt(arr[0]) == animalType) {
				// CODE
				result.add(arr[2]);
			}
		}
		return result;
	}



	/**
	 * 获取投保种类List<String[code, caption]>
	 *
	 * */
	public static List<String[]>	getInsureTypes(int animalType) {
		List<String[]> result = new ArrayList<String[]>();
		for(String[] arr : INSURE_TYPE) {
			if(toInt(arr[0]) == animalType) {
				result.add(new String[]{arr[1], arr[2]});
			}
		}
		return result;
	}


	/**
	 * 获取投保种类Code List<String>
	 *
	 * */
	public static List<String> getInsureTypeCodes(int animalType) {
		List<String> result = new ArrayList<String>();
		for(String[] arr : INSURE_TYPE) {
			if(toInt(arr[0]) == animalType) {
				// CODE
				result.add(arr[1]);
			}
		}
		return result;
	}

	/**
	 * 获取投保种类Code List<Integer>
	 *
	 * */
	public static List<Integer> getInsureTypeIntCodes(int animalType) {
		List<Integer> result = new ArrayList<Integer>();
		for(String[] arr : INSURE_TYPE) {
			if(toInt(arr[0]) == animalType) {
				// CODE
				result.add(toInt(arr[1]));
			}
		}
		return result;
	}

	/**
	 * 获取投保种类名称 List<String>
	 *
	 * */
	public static List<String> geInsureTypeCaptions(int animalType) {
		List<String> result = new ArrayList<String>();
		for(String[] arr : INSURE_TYPE) {
			if(toInt(arr[0]) == animalType) {
				// CODE
				result.add(arr[2]);
			}
		}
		return result;
	}


	/**
	 * 根据CODE获得名称
	 * */
	private static String getCaptionByCode(String[][] constArr, int code) {
		for(String[] arr : constArr) {
			// 投保种类CODE相同时返归相应投保种类名称
			if(toInt(arr[1]) == code) {
				return arr[2];
			}
		}
		return UNKNOWN_RESULT;
	}

	/**
	 * 根据名称获得CODE
	 * */
	private static String getCodeByCaption(String[][] constArr, String caption) {
		if(caption == null || caption.length() == 0)	return null;
		for(String[] arr : constArr) {
			// 投保种类CODE相同时返归相应投保种类名称
			if(caption.equals(arr[2])) {
				return arr[1];
			}
		}
		return UNKNOWN_RESULT;
	}

	/**
	 * 根据名称获得Int CODE
	 * */
	private static int getCodeIntByCaption(String[][] constArr, String caption) {
		return toInt(getCodeByCaption(constArr, caption));
	}


	/**
	 * 根据投保种类CODE获得投保种类名称
	 * */
	public static String getInsureTypeCaptionByCode(int insureType) {
		return getCaptionByCode(INSURE_TYPE, insureType);
	}
	/**
	 * 根据投保种类CODE获得投保种类名称
	 * */
	public static String getInsureTypeCaptionByCode(String insureType) {
		return getCaptionByCode(INSURE_TYPE, toInt(insureType));
	}

	/**
	 * 根据投保种类名称获得投保种类CODE
	 * */
	public static String getInsureTypeCodeByCaption(String insureCaption) {
		return getCodeByCaption(INSURE_TYPE, insureCaption);
	}
	/**
	 * 根据投保种类名称获得投保种类CODE
	 * */
	public static int getInsureTypeCodeIntByCaption(String insureCaption) {
		return getCodeIntByCaption(INSURE_TYPE, insureCaption);
	}


	/**
	 * 根据动物子种类CODE获得动物子种类名称
	 * */
	public static String getAnimalSubTypeCaptionByCode(int animalSubType) {
		return getCaptionByCode(ANIMAL_SUB_TYPE, animalSubType);
	}
	/**
	 * 根据动物子种类CODE获得动物子种类名称
	 * */
	public static String getAnimalSubTypeCaptionByCode(String animalSubType) {
		return getCaptionByCode(ANIMAL_SUB_TYPE, toInt(animalSubType));
	}

	/**
	 * 根据动物子种类名称获得动物子种类CODE
	 * */
	public static String getAnimalSubTypeCodeByCaption(String animalSubCaption) {
		return getCodeByCaption(ANIMAL_SUB_TYPE, animalSubCaption);
	}
	/**
	 * 根据动物子种类名称获得动物子种类CODE
	 * */
	public static int getAnimalSubTypeCodeIntByCaption(String animalSubCaption) {
		return getCodeIntByCaption(ANIMAL_SUB_TYPE, animalSubCaption);
	}


	private static int toInt(String s) {
		try{
			int result = Integer.parseInt(s);
			return result;
		}catch (Exception e) {
			return -1;
		}
	}

	public static void main(String args[]) {
		System.out.println("输出动物类型CODE");
		System.out.println(getAnimalTypeCodes());
		System.out.println(getAnimalTypeIntCodes());

		System.out.println("输出动物类型名称");
		System.out.println(getAnimalTypeCaptions());


		System.out.println("输出动物子种类(驴)");
		System.out.println(getAnimalSubTypes(ANIMAL_TYPE_DONKEY));


		System.out.println("输出动物子种类CodeList(驴)");
		System.out.println(getAnimalSubTypeCodes(ANIMAL_TYPE_DONKEY));
		System.out.println(getAnimalSubTypeIntCodes(ANIMAL_TYPE_DONKEY));

		System.out.println("输出动物子种类名称(驴)");
		System.out.println(getAnimalSubTypeCaptions(ANIMAL_TYPE_DONKEY));

		System.out.println("输出投保种类(驴)");
		System.out.println(getInsureTypes(ANIMAL_TYPE_DONKEY));

		System.out.println("输出投保种类Code(驴)");
		System.out.println(getInsureTypeCodes(ANIMAL_TYPE_DONKEY));
		System.out.println(getInsureTypeIntCodes(ANIMAL_TYPE_DONKEY));

		System.out.println("输出投保种类名称(驴)");
		System.out.println(geInsureTypeCaptions(ANIMAL_TYPE_DONKEY));


		System.out.println("根据投保种类CODE获得投保种类名称(201)");
		System.out.println(getInsureTypeCaptionByCode(201));
		System.out.println("根据投保种类CODE获得投保种类名称(202)");
		System.out.println(getInsureTypeCaptionByCode("202"));


		System.out.println("根据投保种类名称获得投保种类CODE(能繁母牛养殖保险)");
		System.out.println(getInsureTypeCodeIntByCaption("能繁母牛养殖保险"));
		System.out.println(getInsureTypeCodeByCaption("肉牛养殖保险"));


		System.out.println("根据动物子种类CODE获得投保种类名称(303)");
		System.out.println(getAnimalSubTypeCaptionByCode(303));
		System.out.println("根据动物子种类CODE获得投保种类名称(302)");
		System.out.println(getAnimalSubTypeCaptionByCode("302"));


		System.out.println("根据动物子种类名称获得动物子种类CODE(能繁母驴)");
		System.out.println(getAnimalSubTypeCodeByCaption("能繁母驴"));
		System.out.println(getAnimalSubTypeCodeIntByCaption("驴"));

	}

}
