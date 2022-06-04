package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
/**
 * Writes the itinerary
 * into a GPX file
 *
 * @author Imade Bouhamria (339659)
 */
public class GpxGenerator {

    private GpxGenerator(){}

    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e);
        }
    }

    /**
     * Creates the content of the GPX file
     * @param route the route that is going
     *              to be exported
     * @param rProfile the profile of said
     *                 route
     * @return a Document containing all the
     * necessary element of the GPX file
     */
    public static Document createGpx(Route route, ElevationProfile rProfile){

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

        for (PointCh p: route.points()){
            Element rtept = doc.createElement("rtept");
            Element ele   = doc.createElement("ele"  );

            rtept.setAttribute("lat", Double.toString(
                                                        Math.toDegrees(p.lat())));
            rtept.setAttribute("lon", Double.toString(
                                                        Math.toDegrees(p.lon())));

            double position = route.pointClosestTo(p).position();
            ele.setTextContent(         Double.toString(rProfile.elevationAt(position)));

            rte.appendChild(rtept);
            rtept.appendChild(ele);
        }

        return doc;
    }

    /**
     * Writes the GPX Document into a text file
     * @param fileName the text file name
     * @param route the route to be exported
     * @param rProfile the elevation profile
     *                 of said route
     */
    public static void writeGpx(String fileName, Route route, ElevationProfile rProfile){

        Document doc = createGpx(route, rProfile);

        try{
            Writer w = new FileWriter(fileName + ".gpx");

             Transformer transformer = TransformerFactory
                .newDefaultInstance()
                .newTransformer();

             transformer.setOutputProperty(OutputKeys.INDENT, "yes");
             transformer.transform(new DOMSource(doc),
                                   new StreamResult(w));
        } catch (TransformerException e) {
            throw new Error(e);
        }
        catch (IOException ignored) {
        }

    }

}
