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
        boolean secondStepCheck = false;

        for (int i = 0; i < numberOfSamples; i++) {

            sampleList[i] = (float) route.elevationAt(i * length / (float)numberOfSamples);

            //Etape 1 :
            if (!firstStepCheck && !Float.isNaN(sampleList[i])){
                Arrays.fill(sampleList,0,i,sampleList[i]);
                firstStepCheck = true;
            }
        }

        if (!firstStepCheck) {
            Arrays.fill(sampleList,0,numberOfSamples-1,0);
        }


        //Etape 2 :
        if (!Double.isNaN(route.elevationAt(route.length()))) {
            sampleList[numberOfSamples-1] = (float)route.elevationAt(route.length());
            secondStepCheck = true;
        }
        for (int i = numberOfSamples - 2; i >= 0 && !secondStepCheck; i--) {
            if(!Float.isNaN(sampleList[i])){
                Arrays.fill(sampleList , i, numberOfSamples, sampleList[i]);
                secondStepCheck = true;
            }
        }


        //Etape 3 :
        for (int i = 1; i < numberOfSamples; i++) {
            if(Float.isNaN(sampleList[i])){
                for (int j = i; j < numberOfSamples; j++) {
                    if (!Float.isNaN(sampleList[j])) {
                        System.out.print("step for last know value index = ");
                        System.out.print(i-1);
                        System.out.print(" : ");
                        System.out.print((double)1 / (double)(j-i+1));
                        System.out.print(" of lenght : ");
                        System.out.println(j-i+1);
                        for (int k = i-1; k < j; k++) {
                            sampleList[k] = (float)Math2.interpolate(
                                     sampleList[i-1],
                                     sampleList[j],
                                     (double)(k-i+1) / (double)(j-i+1)
                             );
                        }
                        break;
                    }
                }
            }
        }

//        for (double d : sampleList ) {
//            System.out.println(d);
//        }

        return new ElevationProfile(length,sampleList);
    }
}
