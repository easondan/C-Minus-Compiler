package absyn;

abstract public class Exp extends Absyn {
    /**
     *  Add new attribute Dec dtype - reference to a "Dec" node that helps us
     *  find the type information
     **/
     public Dec dtype;
}
