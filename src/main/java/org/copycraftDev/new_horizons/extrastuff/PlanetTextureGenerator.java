package org.copycraftDev.new_horizons.extrastuff;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.copycraftDev.new_horizons.extrastuff.noise.map.Grid;
import org.copycraftDev.new_horizons.extrastuff.noise.map.generator.cellular.CellularAutomataGenerator;
import org.copycraftDev.new_horizons.extrastuff.noise.map.generator.noise.NoiseGenerator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlanetTextureGenerator {
    private final int size;
    private final long seed;
    private final int octaves;
    private final float baseFrequency, lacunarity, persistence;
    private final Color baseMinColor, baseMaxColor;
    private final List<SubColor> subColors;
    private final int noiseRadius, cellularIterations;
    private final float noiseModifier;
    private final ExecutorService executor;
    private static final float TWO_PI = (float)(Math.PI * 2);

    private PlanetTextureGenerator(Builder b) {
        this.size            = b.size;
        this.seed            = b.seed;
        this.octaves         = b.octaves;
        this.baseFrequency   = b.baseFrequency;
        this.lacunarity      = b.lacunarity;
        this.persistence     = b.persistence;
        this.baseMinColor    = b.baseMinColor;
        this.baseMaxColor    = b.baseMaxColor;
        this.subColors       = b.subColors;
        this.noiseRadius     = b.noiseRadius;
        this.noiseModifier   = b.noiseModifier;
        this.cellularIterations = b.cellularIterations;
        this.executor        = Executors.newSingleThreadExecutor(r->new Thread(r,"PlanetTexGen"));
    }

    public CompletableFuture<Void> generateAndRegisterAsync(String modId, String planetName) {
        return CompletableFuture.runAsync(() -> {
            generateAndRegister(modId, planetName);
            executor.shutdown();
        }, executor);
    }

    public void generateAndRegister(String modId, String planetName) {
        Grid baseGrid = new Grid(size);
        NoiseGenerator.generate(baseGrid, noiseRadius, noiseModifier, (int)seed);

        Grid subGrid = new Grid(size);
        CellularAutomataGenerator.generate(subGrid, cellularIterations);

        NativeImage albedo = new NativeImage(NativeImage.Format.RGBA, size, size, false);
        NativeImage height = new NativeImage(NativeImage.Format.RGBA, size, size, false);
        NativeImage normal = new NativeImage(NativeImage.Format.RGBA, size, size, false);

        for(int x=0;x<size;x++){
            for(int y=0;y<size;y++){
                float e = baseGrid.get(x,y);
                for(int o=1;o<octaves;o++){
                    e += persistence * baseGrid.get((int)(x*lacunarity),(int)(y*lacunarity));
                }
                e = clamp(e,0,1);
                Color col = lerpColor(baseMinColor, baseMaxColor, e);
                float s = subGrid.get(x,y);
                for(SubColor sc:subColors){
                    if(s>sc.threshold){
                        float t=(s-sc.threshold)/(1-sc.threshold)*sc.influence;
                        col=lerpColor(col,sc.color,clamp(t,0,1));
                    }
                }
                albedo.setColor(x,y,col.getRGB()|0xFF000000);
                int ie=(int)(e*255);
                int hc=(0xFF<<24)|(ie<<16)|(ie<<8)|ie;
                height.setColor(x,y,hc);
            }
        }

        for(int x=0;x<size;x++)for(int y=0;y<size;y++){
            int n=computeNormal(height,x,y);
            normal.setColor(x,y,(n&0x00FFFFFF)|0xFF000000);
        }

        MinecraftClient.getInstance().execute(() -> {
            register(modId,planetName+"_albedo.png",albedo);
            register(modId,planetName+"_height.png",height);
            register(modId,planetName+"_normal.png",normal);
        });
    }

    private void register(String modId,String fn,NativeImage img){
        NativeImageBackedTexture t=new NativeImageBackedTexture(img);
        Identifier id=Identifier.of(modId,"textures/generated/"+fn);
        MinecraftClient.getInstance().getTextureManager().registerTexture(id,(AbstractTexture)t);
    }

    private int computeNormal(NativeImage hm,int x,int y){
        int s= size, xm=(x-1+s)%s, xp=(x+1)%s, ym=(y-1+s)%s, yp=(y+1)%s;
        float hl=hm.getRed(xm, y)/255f, hr=hm.getRed(xp, y)/255f;
        float hd=hm.getRed(x, ym)/255f, hu=hm.getRed(x, yp)/255f;
        float dx=hr-hl, dy=hu-hd, dz=2f/size;
        float inv=1f/(float)Math.sqrt(dx*dx+dy*dy+dz*dz);
        float nx=-dx*inv, ny=-dy*inv, nz=dz*inv;
        int ir=(int)((nx*0.5f+0.5f)*255), ig=(int)((ny*0.5f+0.5f)*255), ib=(int)((nz*0.5f+0.5f)*255);
        return (ir<<16)|(ig<<8)|ib;
    }

    private static float clamp(float v,float min,float max){return v<min?min:(v>max?max:v);}
    private static Color lerpColor(Color a,Color b,float t){
        int r=(int)(a.getRed()+(b.getRed()-a.getRed())*t);
        int g=(int)(a.getGreen()+(b.getGreen()-a.getGreen())*t);
        int b2=(int)(a.getBlue()+(b.getBlue()-a.getBlue())*t);
        return new Color(r,g,b2);
    }

    public static class SubColor{public final Color color;public final float influence,threshold;public SubColor(Color c,float i,float t){color=c;influence=i;threshold=t;}}

    public static class Builder{
        private int size=512;private long seed=0;private int octaves=4;
        private float baseFrequency=3,lacunarity=2,persistence=0.5f;
        private Color baseMinColor=Color.BLACK,baseMaxColor=Color.WHITE;
        private List<SubColor> subColors=new ArrayList<>();
        private int noiseRadius=16,cellularIterations=3;private float noiseModifier=1f;
        public Builder size(int s){size=s;return this;}public Builder seed(long s){seed=s;return this;}
        public Builder octaves(int o){octaves=o;return this;}public Builder frequency(float f){baseFrequency=f;return this;}
        public Builder lacunarity(float l){lacunarity=l;return this;}public Builder persistence(float p){persistence=p;return this;}
        public Builder baseColors(Color min,Color max){baseMinColor=min;baseMaxColor=max;return this;}
        public Builder addSubColor(Color c,float influence,float threshold){subColors.add(new SubColor(c,influence,threshold));return this;}
        public Builder noiseRadius(int r){noiseRadius=r;return this;}public Builder noiseModifier(float m){noiseModifier=m;return this;}
        public Builder cellularIterations(int i){cellularIterations=i;return this;}public PlanetTextureGenerator build(){return new PlanetTextureGenerator(this);}
    }
}
