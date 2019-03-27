import * as React from 'react';
import VtkContent from './common/content';
import {connect } from 'react-redux';
import { HAS_PERMISSION_FOR } from './permission';
import { render } from './node_modules/@types/react-dom';


declare var $: any;

export interface VtkMtgPlanAidsProps {
  assets: Object[],
  helper: any,
  user_variable:any,
  videos: any,
  meetingId: string
}

const asset = (link,show) => {
  let {title,description,refId,uid,docType, isOutdoorRelated} = link;

  let isAvailable = '';

  if(show){
    isAvailable = ' disabled';
  }

  let isPdf = (docType === 'pdf') || refId.indexOf('.pdf') === (refId.length - '.pdf'.length);

  return (isPdf)
    ?<li key={uid}>
      <div style={{display:'table'}}>
          <div style={{display:'table-cell'}}>
            <a style={{fontWeight:'bold'}} href={refId}  title="Meeting Asset"  target="_blank"  className={"icon "+'pdf '+isAvailable}>
                {title} <span></span>
            </a>
            <p className="info">{description}</p>
          </div>
          {(isOutdoorRelated)
          ? <div style={{display:'table-cell', textAlign:'center'}}>
              <img src='/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png' style={{'width':'60%'}} />
            </div> 
          :null
          }
      </div>
    </li>
    : <li key={uid}>
        <div style={{display:'table'}}>
            <div style={{display:'table-cell'}}>
              <a 
              style={{fontWeight:'bold'}} 
              href={refId}  
              data-reveal-id="modal_popup_video" 
              data-reveal-ajax={`/content/girlscouts-vtk/controllers/vtk.include.modals.modal_youtube.html?resource=${refId}`}
              title="Meeting Asset"
              target="_blank"
              className={"icon "+docType+' '+isAvailable}>
                  {title} <span></span>
              </a>
              <p className="info">{description}</p>
            </div>
            {(isOutdoorRelated)
            ? <div style={{display:'table-cell', textAlign:'center'}}>
                <img src='/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png' style={{'width':'60%'}} />
              </div> 
            :null
            }
        </div>
    </li>
}

// function VtkMtgPlanAids (props: VtkMtgPlanAidsProps) {
class VtkMtgPlanAids extends React.Component<VtkMtgPlanAidsProps, {}>{

   componentDidMount() {
      $(document).foundation(); 
   }


   componentDidUpdate(){
      $(document).foundation();
   }

    public render(){

      let { assets, helper, user_variable, videos, meetingId } = this.props;
    console.log('videos',videos)

    let assetsAll = assets;

    if(meetingId in videos){
      assetsAll = assets.concat(this.props.videos[meetingId])
    }
   
    const permissionCheck = HAS_PERMISSION_FOR()(
      "vtk_troop_haspermision_edit_yearplan_id"
    );



    return (
      <div>
        <VtkContent idName="meeting-aids">
          <h6>Meeting Aids</h6>



          <ul className="__list_of_assets large-block-grid-2 medium-block-grid-2 small-block-grid-2">
            {assetsAll.map((link)=>{
              return asset(link,user_variable.user_current_year!==user_variable.vtk_current_year)
            })}
          </ul>
      
      
          {(permissionCheck)?<a className="add-btn" data-reveal-id="modal_popup" data-reveal-ajax="true" href={`/content/girlscouts-vtk/controllers/vtk.include.modals.modal_meeting_aids.html?elem=${helper.currentDate}`} title="Add meeting aids">
            <i className="icon-button-circle-plus"></i>Add Meeting Aids
          </a>:null}
        </VtkContent>
      </div>
    );

    }

    
}



const mapStateToProps = (state) => {
  return { 
    assets: state.meetingEvents.assets,
    helper: state.helper,
    user_variable: state.user_variable,
    videos: state.videos,
    meetingId: state.meetingEvents.meetingInfo.id
  }
}

export default connect(mapStateToProps)(VtkMtgPlanAids)



