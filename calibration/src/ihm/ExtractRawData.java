package ihm;

import java.io.File;
import java.util.List;
import java.util.Iterator;
import org.jdom2.*;
import org.jdom2.input.*;

public class ExtractRawData {

	private org.jdom2.Document document;
	private Element racine;

	public void parse(String toParse) {
		// On cr�e une instance de SAXBuilder
		SAXBuilder sxb = new SAXBuilder();
		try {
			// On cr�e un nouveau document JDOM avec en argument le fichier XML
			// Le parsing est termin� ;)
			document = sxb.build(new File(toParse));
		} catch (Exception e) {
		}

		// On initialise un nouvel �l�ment racine avec l'�l�ment racine du
		// document.
		racine = document.getRootElement();
	}
	
	public String infoNoeud(String nomNoeud){
	//On saute systematiquement le premier noeud du fichier
		List<Element> listdl_settings = racine.getChildren("dl_settings");
		listdl_settings = listdl_settings.get(0).getChildren("dl_settings");
		String buf = null;
		int dlSettingsId1 = 0;
		int dlSettingsId2 = 0;
		Iterator<Element> i = listdl_settings.iterator();
		boolean found = false;
		do {
			Iterator<Element> j = (i.next().getChildren("dl_settings")).iterator();
			dlSettingsId1++;
			do {
					found = found || (j.next().getAttributeValue("var").toString().equals("telemetry_mode_Main"));
					dlSettingsId2++;
			} while (!found && j.hasNext());
		} while (i.hasNext() && !found);
		//On redescend pour tenir le bon noeud qui a d�j� �t� mang�.
		i = listdl_settings.iterator();
		for(int k=0;k<dlSettingsId1;k++){
			i.next();
		}
		i = i.next().getChildren().iterator();
		for(int k=0;k<dlSettingsId2;k++){
			i.next();
		}
		buf = i.next().getAttributeValue("values").toString();
		return buf;
		}
		
		//on tient donc i au noeud dlSettings contenant les modes de telemetry
		//on recupere la chaine qui va bien
		

	public void static main(String args[]){
		
	}
