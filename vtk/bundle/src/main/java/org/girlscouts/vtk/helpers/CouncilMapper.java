package org.girlscouts.vtk.helpers;

public interface CouncilMapper {
	String getCouncilBranch(String id);

	/**
	 * @return the default branch
	 */
	String getCouncilBranch();

	String getCouncilUrl(String id);

	/**
	 * @return the default council url
	 */
	String getCouncilUrl();
	
	String getCouncilName(String concilCode);
}
