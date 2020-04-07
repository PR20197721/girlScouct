import javax.jcr.query.*
import javax.jcr.*
import java.util.logging.Logger
import java.util.*
import javax.jcr.Node.*
import org.apache.commons.lang3.*

def root = "/etc/designs";
def compName = "foundation/components/form/dropdown-no-edit";
def values = [];

//get all nodes having dropdown-no-edit component as a property value and remove it
getNode(root).recurse {rootNode ->
    if (rootNode.hasProperty('components')) {
        Property prop = rootNode.getProperty("components");
        if (prop.isMultiple()) {
            values = prop.getValues();
            def val = values as String[];
            if (Arrays.asList(val).contains(compName)) {
                val = ArrayUtils.removeAllOccurences(val, compName);
                rootNode.setProperty("components", val);
            }
        } else {
            final def compPath = prop.getString();
            if (compPath.equals(compName)) {
                prop.remove();
            }
        }
        rootNode.save();
    }
}
