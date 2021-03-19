package ga_classes;
import java.util.Comparator;


public class ChromosomeComparator implements Comparator<Chromosome>
{
    public static ChromosomeComparator obj = new ChromosomeComparator();

    public ChromosomeComparator()
    {

    }

    /* returns -1 if c1 is smaller
     * 1 if c1 is larger
     * and 0 if both chromosomes are equal
     */

    public int compare(Chromosome c1, Chromosome c2)
    {
        int c1Total = c1.getTotal();
        int c2Total = c2.getTotal();

        if (c1Total < c2Total)
        {
            return -1;
        }

        if (c1Total > c2Total)
        {
            return 1;
        }

        return 0;
    }

    /*	public int compare(Chromosome c1, Chromosome c2)
        {
            if(c1.getUnfitnessValue() < c2.getUnfitnessValue())
            {
                return -1;
            }

            else if(c1.getUnfitnessValue() > c2.getUnfitnessValue())
            {
                return 1;
            }

            else if(c1.getFitnessValue() < c2.getFitnessValue())
            {
                return -1;
            }

            else if(c1.getFitnessValue() > c2.getFitnessValue())
            {
                return 1;
            }

            return 0;
        }*/
}
