package rent;

import static rent.Generator.*;
import rps.RentEz;
import rps.Template;

public class CreateTemplate {
    private Template template;
    RentEz rentEz;
    
    public CreateTemplate(RentEz app, String name) {
        this.rentEz = app;
        //if the template already exists then use it else create a new one
        template = this.rentEz.getTemplate(name);
        if(template==null)
            template = this.rentEz.addTemplate(name);
    }
    public void oneForPeople (String itemName, float numPeople){
    	click("templates");
    	click("new template");
    	withAddText("template item",""+itemName);
    	withAddText("template number of people",""+numPeople);
    	click("create template");
        template.addItem(rentEz.getRentalItemType(itemName), numPeople);
    }
	public void oneForForPeople(String itemName,String itemUse,float numPeople) throws Exception{
    	click("templates");
    	click("new template");
    	withAddText("template item",""+itemName);
    	clickRadio(itemUse);
    	withAddText("template number of people",""+numPeople);
    	click("create template");
		if(itemUse.equalsIgnoreCase("booking or renting")){
			template.addItem(rentEz.getRentalItemType(itemName),numPeople);
		}
		if(itemUse.equalsIgnoreCase("sale")){
			template.addItem(rentEz.getSalesItemType(itemName),numPeople);
		}
	}
}
