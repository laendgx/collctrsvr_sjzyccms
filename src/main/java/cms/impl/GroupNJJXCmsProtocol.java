package cms.impl;

import com.boco.cmsprotocolBody.IconList;
import com.boco.cmsprotocolBody.ItemList;
import com.boco.cmsprotocolBody.PlayList;
import com.boco.cmsprotocolBody.WordList;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * 南京金晓情报板播放表类
 * @author dgx
 *
 */
public class GroupNJJXCmsProtocol {

	//json字符串与对象之间的转换
	public static<T> Object JSONToObj(String jsonStr,Class<T> obj) {
		T t = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			t = objectMapper.readValue(jsonStr,
					obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * 将通用的播放表转换为可变情报板南京金晓的播放表
	 */
	public String buildProtocal(String PlaylistTemp) {
		String result = "";
		try {
		PlayList playListValue =  (PlayList)JSONToObj(PlaylistTemp, PlayList.class);//下发播放表信息

		int id = 0;
		result = "[playlist]" + "\r\n" + 
				"item_no=" + playListValue.getItemList().size() +  "\r\n";
		for(ItemList entity :  playListValue.getItemList()){
			String itemProtocal = this.buildItemProtocal(entity, playListValue.getDpt());
			itemProtocal = "item" + id + "=" + itemProtocal + "\r\n";
			result += itemProtocal;
			id++;
		}
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}

	/**
	 * 将单条播放表转换为情报板协议
	 * @return
	 */
	private String buildItemProtocal(ItemList item, int dispScrType){
			Integer delay = item.getDelay() * 100;
			Integer trans = item.getMode();
			//para当出字方式为 0 或 1 时，speed 无用；当出字方式为 2-21 时，speed 表示出字速度，范围 0-49，默认 为 0，0 表示最快。
			Integer speed = 0;
			
			if (trans > 5){
				trans = 1;
			}
			
			String protocolString = delay + "," + trans + "," + speed + ",";
			
			List<IconList> graphs = item.getGraphList();
			String graphProtocol = graphParaToString(graphs);
			protocolString += graphProtocol;
			
			protocolString += this.wordParaToString(item,dispScrType);
			return protocolString;		
	}
	
	/**
	 * 将可变情报板的文字参数转换为协议字符串
	 * @param //Itemlist
	 * @return
	 */
	private String wordParaToString(ItemList item, int dispScrType){
		String result = "";
		if (item == null){
			return result;
		}
		
		List<WordList> list = item.getWordList();
		if (list == null || list.size() == 0){
			return result;
		}
		
		for(WordList para : list){
			if (para.getWx() == null){
				result += "\\C000";
			}else{
				result += "\\C" + this.intTo3SizeString(para.getWx());
			}
			
			if (para.getWy() == null){
				result += "000";
			} else{
				result += this.intTo3SizeString(para.getWy());
			}
			
			switch (dispScrType) {
			//双基色或者全彩
			case 0:		
				//字符颜色
				if (item.getFc() == null){
					result += "\\c255255000000";
				}else {
					result += "\\c" + this.hexColor2RGB(item.getFc());
				}
				break;
			//琥珀色
			case 1:	
				result += "\\c000000000255";
				break;
			//无颜色
			case 2:	
				result += "\\c000000000255";
				break;
			default:
				break;
			}
			
			//字体
			if (item.getFn() == null){
				result += "\\fh";
			} else {
				result += "\\f" + item.getFn();
			}
			
			//字体大小－高度  +  字体大小－宽度
			if (item.getFs() == null){
				result += "32";
				result += "32";
			} else {
				result += String.format("%02d", item.getFs());
				result += String.format("%02d", item.getFs());
			}

			//字间距
			result += "\\S" + "0";
			
			//文字内容
			if (para.getWc() != null){
				result += para.getWc();
			}
		}
		
		return result;
	}
	
	/**
	 * 将可变情报板图标参数转换为协议字符串
	 */
	private String graphParaToString(List<IconList> list) {
		String result = "";
		if (list == null || list.size() == 0)
			return result;

		for(IconList icon : list){
		result +=  "\\C" + this.intTo3SizeString(icon.getGx()) + this.intTo3SizeString(icon.getGy()) +
			   "\\B" + icon.getGid();
		}
		return result;
	}


	/**
	 * 将整数转换成3位的字符串
	 * @param value
	 * @return
	 */
	private String intTo3SizeString(Integer value) {
		String str = "";
		if (value == null) {
			str = "000";
		} else {
			str = String.format("%03d", value);
		}
		return str;
	}

	/**
	 * 将颜色值转换为rgb字符串
	 * @return
	 */
	private String hexColor2RGB(String hex){
		String result = "";
		switch (hex)
		{
			case "r":
				result="255000000000";
				break;
			case "g":
				result="000255000000";
				break;
			case "y":
				result="255000000000";
				break;
		}

//		String str = hex.replaceAll("^#", "");
//
//		try {
//			int color = Integer.valueOf(str, 16);
//			short b = (short) (color & 0xFF);
//			short g = (short) ((color >> 8) & 0xFF);
//			short r = (short) ((color >> 16) & 0xFF);
//			String bsz = String.format("%03d", b);
//			String gsz = String.format("%03d", g);
//			String rsz = String.format("%03d", r);
//
//			result = rsz + gsz + bsz + "000";
//		} catch (Exception ex) {
//			result = "255255000000";
//		}
		return result;
	}

	
}
