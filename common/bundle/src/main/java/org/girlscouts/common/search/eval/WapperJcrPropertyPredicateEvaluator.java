package org.girlscouts.common.search.eval;

import java.util.Comparator;
import java.util.Map;

import javax.jcr.query.Row;

import org.apache.felix.scr.annotations.Component;

import com.day.cq.search.Predicate;
import com.day.cq.search.eval.EvaluationContext;
import com.day.cq.search.eval.JcrPropertyPredicateEvaluator;
import com.day.cq.search.eval.PredicateEvaluator;
import com.day.cq.search.facets.FacetExtractor;

@Component(metatype = false, factory="com.day.cq.search.eval.PredicateEvaluator/gsproperty")
public class WapperJcrPropertyPredicateEvaluator implements PredicateEvaluator {
    private JcrPropertyPredicateEvaluator impl;
    
    public WapperJcrPropertyPredicateEvaluator() {
	this.impl = new JcrPropertyPredicateEvaluator();
    }

    public boolean canFilter(Predicate predicate, EvaluationContext context) {
	return impl.canFilter(wrapPredicate(predicate), context);
    }

    public boolean canXpath(Predicate predicate, EvaluationContext context) {
	return impl.canXpath(wrapPredicate(predicate), context);
    }

    public FacetExtractor getFacetExtractor(Predicate predicate,
	    EvaluationContext context) {
	return null;
    }

    public Comparator<Row> getOrderByComparator(Predicate predicate,
	    EvaluationContext context) {
	return impl.getOrderByComparator(wrapPredicate(predicate), context);
    }

    public String[] getOrderByProperties(Predicate predicate, EvaluationContext context) {
	return impl.getOrderByProperties(wrapPredicate(predicate), context);
    }

    public String getXPathExpression(Predicate predicate, EvaluationContext context) {
	return impl.getXPathExpression(wrapPredicate(predicate), context);
    }

    public boolean includes(Predicate predicate, Row row, EvaluationContext context) {
	return impl.includes(predicate, row, context);
    }

    public boolean isFiltering(Predicate predicate, EvaluationContext context) {
	return impl.isFiltering(wrapPredicate(predicate), context);
    }
    
    private Predicate wrapPredicate(Predicate orig) {
	Predicate result = new Predicate("property", "property");
	Map<String, String> parameters = orig.getParameters();
	for (String key : parameters.keySet()) {
	    String value = parameters.get(key).replaceAll("gsproperty", "property");
	    key = key.replaceAll("gsproperty", "property");
	    result.set(key, value);
	}
	return result;
    }

}