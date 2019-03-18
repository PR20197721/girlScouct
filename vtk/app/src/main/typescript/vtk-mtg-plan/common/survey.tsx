import * as React from 'react';
import { connect } from 'react-redux';

export interface VtkSurveyProps {
	survey:any
}

import './survey.scss';
export interface VtkSurveyState {}

class VtkSurvey extends React.Component<
	VtkSurveyProps,
	VtkSurveyState
> {
	constructor(props: VtkSurveyProps) {
		super(props);
		this.state = {};
	}
	

	click(link){
		// window.location.href = link
		window.open(link,'_blank');
	}
    
	render() {

		const { survey } = this.props;

		return (survey.meetingId)?(
			<div
				className={'vtk-survey columns small-24'}
				style={{ cursor: 'pointer' }}
			>
				<div className="text-center columns small-24 medium-push-1 medium-3 small-text-center medium-text-left">
					<img
						src={
							'/etc/designs/girlscouts-vtk/clientlibs/css/images/survey_icon.png'
						}
						alt={survey.description}
						title={survey.description}
						style={{ height: '34px', padding: '0px 10px' }}
					/>
				</div>

				<div
					className={
						'columns small-24 medium-push-1 medium-13 small-text-center medium-text-left'
					}
					style={{ padding: '11px 0px', margiLeft: '-5px' }}
				>
					<b>{survey.title}</b> {survey.description}
				</div>

				<div className="columns small-24 medium-4 medium-pull-1 small-text-center medium-text-left">
					<div>
						<button className={'tiny'} onClick={()=>this.click(survey.linkHref)}>{survey.buttonTitle}</button>
					</div>
				</div>
			</div>
		):null;
	}
}


let modal = state => {
	return { survey: state.survey };
};

export default connect(modal)(VtkSurvey);

