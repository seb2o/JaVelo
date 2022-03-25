package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

/**
 * @author Sébastien Boo (345870)
 * @author Edgar Gonzalez (328095)
 */
public final class ElevationProfileComputer {

    //classe ininstanciable
    private ElevationProfileComputer() {}


    /**
     * retourne le profil en long d'une route.
     * @param route la route dont le profil est a calculer
     * @param maxStepLength l'écart maximum entre deux échantillons du profil.
     * @return le profil en long de la route
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength){
        Preconditions.checkArgument(maxStepLength > 0);
        double length = route.length();
        int numberOfSamples = (int)Math.ceil(length / maxStepLength) + 1;
        float[] sampleList = new float[numberOfSamples];

        boolean firstStepCheck = false;
        for (int i = 0; i < numberOfSamples; i++) {
            sampleList[i] = (float) route.elevationAt(i * length / numberOfSamples); //todo ca me semble bizarre de devoir cast en float mais pk pas

            //Etape 1 :
            if (!firstStepCheck && !Float.isNaN(sampleList[i])){
                Arrays.fill(sampleList,0,i,sampleList[i]);
                firstStepCheck = true;
            }
        }

        //Etape 2 :
        for (int i = numberOfSamples - 1; i >= 0; i--) {
            if(!Float.isNaN(sampleList[i])){
                Arrays.fill(sampleList , i, numberOfSamples, sampleList[i]);
            }
        }

        //Etape 3 :
        for (int i = 0; i <= numberOfSamples; i++) {
            if(Float.isNaN(sampleList[i])){
                for (int j = i; j < numberOfSamples; j++) {
                    if (!Float.isNaN(sampleList[j])) {
                        for (int k = i; k < j; k++) {
                            Math2.interpolate(sampleList[i-1], sampleList[j],(double)(k-i) / (double)(j-i) ); //todo si ça marche du premier coup je suis un dieu. Flemme de plus réfléchir à ça.
                        }
                    }
                }
            }
        }

        return new ElevationProfile(length,sampleList);
    }
}
