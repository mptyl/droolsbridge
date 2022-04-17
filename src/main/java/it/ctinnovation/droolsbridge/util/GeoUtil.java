package it.ctinnovation.droolsbridge.util;

import it.ctinnovation.droolsbridge.model.Asset;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.DistanceCalculator;
import org.locationtech.spatial4j.shape.Point;

public class GeoUtil {
    public static double calculateDistance (double latitude, double longitude, Asset a){
        Point start=SpatialContext.GEO.getShapeFactory().pointXY(latitude, longitude);
        Point end=SpatialContext.GEO.getShapeFactory().pointXY(a.getPosition().getLatitude(), a.getPosition().getLongitude());
        DistanceCalculator dc =
                SpatialContext.GEO.getDistCalc();
        return dc.distance(start,end);
    }
}
