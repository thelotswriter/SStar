package Attitudes;

import Clustering.DistantDatum;

import java.util.ArrayList;
import java.util.Collection;

public class AttitudeVector implements DistantDatum
{
    private double[] vector;

    public AttitudeVector()
    {
        vector = new double[4];
    }

    public AttitudeVector(AttitudeVector otherAttitudeVector)
    {
        vector = new double[4];
        vector[0] = otherAttitudeVector.vector[0];
        vector[1] = otherAttitudeVector.vector[1];
        vector[2] = otherAttitudeVector.vector[2];
        vector[3] = otherAttitudeVector.vector[3];
    }

    public AttitudeVector(double greedy, double placate, double cooperate, double absurd)
    {
        vector = new double[4];
        vector[0] = greedy;
        vector[1] = placate;
        vector[2] = cooperate;
        vector[3] = absurd;
        normalize();
    }

    public double getGreedy()
    {
        return vector[0];
    }

    public double getPlacate()
    {
        return vector[1];
    }

    public double getCooperate()
    {
        return vector[2];
    }

    public double getAbsurd()
    {
        return vector[3];
    }

//    public void setCooperate(double cooperate)
//    {
//        vector[2] = cooperate;
//        normalize();
//    }
//
//    public void setGreedy(double greedy)
//    {
//        vector[0] = greedy;
//        normalize();
//    }
//
//    public void setPlacate(double placate)
//    {
//        vector[1] = placate;
//        normalize();
//    }
//
//    public void setAbsurd(double absurd)
//    {
//        vector[3] = absurd;
//        normalize();
//    }

    public void setAttitudeVector(double greedy, double placate, double cooperate, double absurd)
    {
        vector = new double[4];
        vector[0] = greedy;
        vector[1] = placate;
        vector[2] = cooperate;
        vector[3] = absurd;
        normalize();
    }

    private boolean isZero()
    {
        return vector[0] == 0.0 && vector[1] == 0.0 && vector[2] == 0.0 && vector[3] == 0.0;
    }

    private void normalize()
    {
        if(vector[0] > vector[1])
        {
            vector[0] = Math.min(1, Math.max(0, vector[0] - vector[1]));
            vector[1] = 0;
        } else if(vector[1] > vector[0])
        {
            vector[1] = Math.min(1, Math.max(0, vector[1] - vector[0]));
            vector[0] = 0;
        } else
        {
            vector[0] = 0;
            vector[1] = 0;
        }
        if(vector[2] > vector[3])
        {
            vector[2] = Math.min(1, Math.max(0, vector[2] - vector[3]));
            vector[3] = 0;
        } else if(vector[3] > vector[2])
        {
            vector[3] = Math.min(1, Math.max(0, vector[3] - vector[2]));
            vector[2] = 0;
        } else
        {
            vector[2] = 0;
            vector[3] = 0;
        }
//        if(!isZero())
//        {
//            double sum = 0;
//            sum += Math.abs(vector[0]);
//            sum += Math.abs(vector[1]);
//            sum += Math.abs(vector[2]);
//            sum += Math.abs(vector[3]);
//            vector[0] = Math.abs(vector[0]) / sum;
//            vector[1] = Math.abs(vector[1]) / sum;
//            vector[2] = Math.abs(vector[2]) / sum;
//            vector[3] = Math.abs(vector[3]) / sum;
//        }
    }

    public double distanceFrom(AttitudeVector otherAttitudeVector)
    {
        double workingDist = 0;
        double gp1 = vector[0] - vector[1];
        double gp2 = otherAttitudeVector.vector[0] - otherAttitudeVector.vector[1];
        double ca1 = vector[2] - vector[3];
        double ca2 = otherAttitudeVector.vector[2] - otherAttitudeVector.vector[3];
        workingDist += (gp1 - gp2) * (gp1 - gp2);
        workingDist += (ca1 - ca2) * (ca1 - ca2);
//        workingDist += (vector[0] - otherAttitudeVector.vector[0]) * (vector[0] - otherAttitudeVector.vector[0]);
//        workingDist += (vector[1] - otherAttitudeVector.vector[1]) * (vector[1] - otherAttitudeVector.vector[1]);
//        workingDist += (vector[2] - otherAttitudeVector.vector[2]) * (vector[2] - otherAttitudeVector.vector[2]);
//        workingDist += (vector[3] - otherAttitudeVector.vector[3]) * (vector[3] - otherAttitudeVector.vector[3]);
        return Math.sqrt(workingDist);
    }

    public double getDistance(DistantDatum otherDatum)
    {
        if(otherDatum instanceof AttitudeVector)
        {
            return distanceFrom((AttitudeVector) otherDatum);
        } else
        {
            return Double.MAX_VALUE;
        }
    }

    public static AttitudeVector average(AttitudeVector attitudeVector1, AttitudeVector attitudeVector2)
    {
        Collection<AttitudeVector> attitudeVectors = new ArrayList<>();
        attitudeVectors.add(attitudeVector1);
        attitudeVectors.add(attitudeVector2);
        return average(attitudeVectors);
    }

    public static AttitudeVector average(Collection<AttitudeVector> attitudeVectors)
    {
        double[] attitudes = new double[4];
        double nAVs = (double) attitudeVectors.size();
        for(AttitudeVector attitudeVector : attitudeVectors)
        {
            attitudes[0] += attitudeVector.vector[0] / nAVs;
            attitudes[1] += attitudeVector.vector[1] / nAVs;
            attitudes[2] += attitudeVector.vector[2] / nAVs;
            attitudes[3] += attitudeVector.vector[3] / nAVs;
        }
        return new AttitudeVector(attitudes[0], attitudes[1], attitudes[2], attitudes[3]);
    }

    public static AttitudeVector extrapolate(AttitudeVector baseAV, AttitudeVector midAV)
    {
        double gp1 = baseAV.getGreedy() - baseAV.getPlacate();
        double gp2 = midAV.getGreedy() - midAV.getPlacate();
        double ca1 = baseAV.getCooperate() - baseAV.getAbsurd();
        double ca2 = midAV.getCooperate() - midAV.getAbsurd();
        double deltaGP = gp1 - gp2;
        double deltaCA = ca1 - ca2;
        double gp3 = gp2 - deltaGP;
        double ca3 = ca2 - deltaCA;
        double[] av = new double[4];
        if(gp3 > 0)
        {
            av[0] = Math.min(1.0, gp3);
        } else
        {
            av[1] = Math.min(1.0, Math.abs(gp3));
        }
        if(ca3 > 0)
        {
            av[2] = Math.min(1.0, ca3);
        } else
        {
            av[3] = Math.min(1.0, Math.abs(ca3));
        }
        return new AttitudeVector(av[0], av[1], av[2], av[3]);
    }

    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        } else if(o instanceof AttitudeVector)
        {
            AttitudeVector otherAV = (AttitudeVector) o;
            return distanceFrom(otherAV) == 0.0;
        } else
        {
            return false;
        }
    }

}
