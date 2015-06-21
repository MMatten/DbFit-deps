package rent;

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
        template.addItem(rentEz.getRentalItemType(itemName), numPeople);
    }
	public void oneForForPeople(String itemName,String itemUse,float numPeople) throws Exception{
		if(itemUse.equalsIgnoreCase("booking or renting")){
			template.addItem(rentEz.getRentalItemType(itemName),numPeople);
		}
		if(itemUse.equalsIgnoreCase("sale")){
			template.addItem(rentEz.getSalesItemType(itemName),numPeople);
		}
	}
}
