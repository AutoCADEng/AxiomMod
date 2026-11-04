package eng.cad.auto.engine.client.render;

import net.minecraft.util.Mth;

public class Wave{
    float wavelength = 0.0f;
    float amplitude = 0.0f;
    float offset = 0.0f;
    public Wave(){}

    public Wave setWavelength(float val){
        wavelength = val;
        return this;
    }
    public Wave setAmplitude(float val){
        amplitude = val;
        return this;
    }

    public Wave setOffset(float val){
        offset = val;
        return this;
    }

    public float evaluate(float val, float beamWidth){
        return Math.max(beamWidth*0.5f* Mth.sin((2.0F * Mth.PI * (offset+val)) / wavelength), beamWidth*0.1f) ;
    }
}