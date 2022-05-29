package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Path;
import java.util.Iterator;

import static java.nio.file.Files.newBufferedWriter;

/**
 * @author Edgar Gonzalez (328095)
 * @author Sébastien Boo (345870)
 */

/*
 * Classe statique permettant d'exporter un intinéraire au format GPX.
 */
public final class GpxGenerator {

    private GpxGenerator() {}


    /**
     * Permet de formater un itinéraire sous un format xml
     * @param route l'itinéraire a formater
     * @param profile le profil de l'itinéraire
     * @return un Document contenant les arètes de l'itinéraire sous forme de balies xml
     */
    public static Document createGpx(Route route, ElevationProfile profile) {

        Document doc = newDocument();

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        Iterator<Edge> i = route.edges().iterator();
        double currentPosition = 0;
        for (PointCh p : route.points()) {
            Element rtept = doc.createElement("rtept");
            rtept.setAttribute("lat", String.valueOf(Math.toDegrees(p.lat())));
            rtept.setAttribute("lon", String.valueOf(Math.toDegrees(p.lon())));
            Element ele = doc.createElement("ele");
            ele.setTextContent(String.valueOf(profile.elevationAt(currentPosition)));
            rtept.appendChild(ele);
            rte.appendChild(rtept);
            if (i.hasNext()) {
                currentPosition+=i.next().length();
            }
        }


        return doc;
    }

    /**
     * Permet de créer le fichier gpx correspondant à un itinéraire
     * @param fileName le nom du fichier gpx a créer
     * @param route l'itinéraire à sauvegarder dans le fichier
     * @param profile le profil de l'itinéraire
     */
    public static void writeGpx(String fileName, Route route, ElevationProfile profile) throws IOException {

        Document doc = createGpx(route,profile);
        Writer w =  newBufferedWriter(Path.of(fileName));

        try{
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w)
            );
            //todo close quelque chose ?
        } catch (TransformerException e) {
            throw new Error();
        }


    }

    /**
     * Crée un nouveau Document par défaut.
     */
    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }
}
