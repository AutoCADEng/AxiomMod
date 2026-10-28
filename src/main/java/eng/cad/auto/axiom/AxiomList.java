package eng.cad.auto.axiom;

import eng.cad.auto.axiom.registry.custom.AxiomRegistry;

import java.util.ArrayList;

public class AxiomList {


    public static void init(){
        addAxiom("cohesio", 0xAAFFB3BA, AxiomType.UNITFORCE); //pastel red
        addAxiom("composito", 0xAAFFDFBA, AxiomType.UNITFORCE); //pastel orange
        addAxiom("proprietera", 0xAAFFDFBA, AxiomType.UNITFORCE); //pastel yellow
        addAxiom("radiato", 0xAAFFFFBA, AxiomType.UNITFORCE); //pastel green
        addAxiom("gravitas", 0xAABAE1FF, AxiomType.UNITFORCE); //pastel blue
        addAxiom("polaris", 0xAADBDCFF, AxiomType.UNITFORCE); //pastel purple
        addAxiom("vitea", 0xAAFB2842, AxiomType.UNIT); // dusty red?
        addAxiom("calor",0xAAEFA643, AxiomType.UNIT); // pumpkin orange
        addAxiom("mutatio", 0xAA5522CC, AxiomType.UNIT); //blueish purple
        addAxiom("ordo", 0xAACCCCCC, AxiomType.UNIT); //white
        addAxiom("soluto",0xAAB2DBBF, AxiomType.UNIT); //jade green
        addAxiom("motus",0xAA999999, AxiomType.UNIT);// grey
        addAxiom("crescentia",0xAA66AA22, AxiomType.UNIT); // herbal green
        addAxiom("forma",0xAA2266AA, AxiomType.UNIT); // light blue
        addAxiom("inversio",0xAA000000, AxiomType.MODIFIER); // black
        addAxiom("potentia",0xAAFFFFFF, AxiomType.MODIFIER);
        addAxiom("trans",0xAAFFE599, AxiomType.MODIFIER);
        for (Axiom axiom: new ArrayList<>(AxiomRegistry.axiomList.values())){
            if (axiom.type() == AxiomType.UNIT) {
                Axiom inversio_axiom = addAxiom("de"+axiom.name(), axiom, AxiomRegistry.axiomList.get("inversio"));
                addAxiom("potenti"+axiom.name(), axiom, AxiomRegistry.axiomList.get("potentia"));
                addAxiom("depotenti"+axiom.name(), inversio_axiom, AxiomRegistry.axiomList.get("potentia"));
            }
        }

        for (Axiom axiom: new ArrayList<>(AxiomRegistry.axiomList.values())){
            if (axiom.type() == AxiomType.UNITFORCE) {
                addAxiom("de"+axiom.name(), axiom, AxiomRegistry.axiomList.get("inversio"));
            }
        }
    }

 public static Axiom addAxiom(String name, int color, AxiomType type){
     Axiom axiom = new Axiom(name, color, type);
     AxiomRegistry.axiomList.put(name, axiom);
     AxiomRegistry.registerElement(axiom);
     return axiom;
 }

 public static Axiom addAxiom(String name, Axiom reactant, Axiom reactant_2){
     int color1 = reactant.color();
     int color2 = reactant_2.color();
     int alpha = hybridizeColor(color1, color2, 0xFF000000,24);
     int red = hybridizeColor(color1, color2, 0x00FF0000,16);
     int green = hybridizeColor(color1, color2, 0x0000FF00,8);
     int blue = hybridizeColor(color1, color2, 0x000000FF, 0);
     return addAxiom(name, alpha|red|green|blue, AxiomType.COMPOUND);
 }

 public static int hybridizeColor(int value_a, int value_b, int bitMask, int bitShift){
     int channel_a = (value_a&bitMask)>>>bitShift;
     int channel_b = (value_b&bitMask)>>>bitShift;
     int average = (channel_a+channel_b)/2;
     return average<<bitShift;
 }
}
