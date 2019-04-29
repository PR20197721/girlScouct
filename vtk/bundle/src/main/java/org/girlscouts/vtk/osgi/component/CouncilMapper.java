package org.girlscouts.vtk.osgi.component;

public interface CouncilMapper {
	String getCouncilBranch(String id);
	String getCouncilBranch();
	String getCouncilUrl(String id);
	String getCouncilName(String concilCode);
}
