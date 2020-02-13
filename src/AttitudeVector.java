public class AttitudeVector
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

    public AttitudeVector(double cooperate, double greedy, double placate, double absurd)
    {
        vector = new double[4];
        vector[0] = cooperate;
        vector[1] = greedy;
        vector[2] = placate;
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

    public void setCooperate(double cooperate)
    {
        vector[2] = cooperate;
        normalize();
    }

    public void setGreedy(double greedy)
    {
        vector[0] = greedy;
        normalize();
    }

    public void setPlacate(double placate)
    {
        vector[1] = placate;
        normalize();
    }

    public void setAbsurd(double absurd)
    {
        vector[3] = absurd;
        normalize();
    }

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
        if(!isZero())
        {
            double sum = 0;
            sum += Math.abs(vector[0]);
            sum += Math.abs(vector[1]);
            sum += Math.abs(vector[2]);
            sum += Math.abs(vector[3]);
            vector[0] = Math.abs(vector[0]) / sum;
            vector[1] = Math.abs(vector[1]) / sum;
            vector[2] = Math.abs(vector[2]) / sum;
            vector[3] = Math.abs(vector[3]) / sum;
        }
    }

}
