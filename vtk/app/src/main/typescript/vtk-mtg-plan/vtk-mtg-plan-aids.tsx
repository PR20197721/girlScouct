import * as React from 'react';
import VtkContent from './common/content';
import { connect } from 'react-redux';
import { HAS_PERMISSION_FOR } from './permission';

declare var $: any;
declare global {
    interface Window {
        vtkTrackerPushAction: any;
    }
}

export interface VtkMtgPlanAidsProps {
    assets: Object[],
    helper: any,
    user_variable: any,
    videos: any,
    meetingId: string,
    meetingUid: string,
    section: string
}

const asset = (link, show) => {
    let { title, description, refId, uid, docType, isOutdoorRelated, isGlobalRelated, isVirtualRelated } = link;

    let isAvailable = show ? 'disabled' : '';
    let isPdf = (docType === 'pdf') || refId.endsWith('.pdf');
    let isVideo = docType === 'movie';
    let isLink = docType === 'link';
    let image = '';
    if (isOutdoorRelated) image = 'outdoor.png';
    if (isGlobalRelated) image = 'global_selected.png';
    if (isVirtualRelated) image = 'virtual_selected.png';

    return <li key={uid}>
        <div style={{ display: 'table' }}>
            <div style={{ display: 'table-cell' }}>
                {isPdf &&
                    <a style={{ fontWeight: 'bold' }} href={refId} title="Meeting Asset" target="_blank" className={`icon pdf ${isAvailable}`}>
                        {title} <span></span>
                    </a>
                }
                {isVideo &&
                    <a
                        style={{ fontWeight: 'bold' }}
                        href={refId}
                        data-reveal-id="modal_popup_video"
                        data-reveal-ajax={`/content/girlscouts-vtk/controllers/vtk.include.modals.modal_youtube.html?resource=${refId}`}
                        title="Meeting Asset"
                        target="_blank"
                        className={`icon ${docType} ${isAvailable}`}>
                        {title} <span></span>
                    </a>
                }
                {isLink &&
                    <a style={{ fontWeight: 'bold' }} href={refId} title="Meeting Asset" target="_blank" className={`icon ${docType} ${isAvailable}`}>
                        {title} <span></span>
                    </a>
                }
                <p className="info">{description}</p>
            </div>
            {image.length > 0 &&
                <div style={{ display: 'table-cell', textAlign: 'center' }}>
                    <img src={`/etc/designs/girlscouts-vtk/clientlibs/css/images/${image}`} style={{ 'width': '60%' }} />
                </div>
            }
        </div>
    </li>
};

// function VtkMtgPlanAids (props: VtkMtgPlanAidsProps) {
class VtkMtgPlanAids extends React.Component<VtkMtgPlanAidsProps, {}> {
    title: string;
    url: string;

    componentDidMount() {
        $(document).foundation();
    }


    componentDidUpdate() {
        $(document).foundation();
    }

    // This syntax ensures `this` is bound within addLink.
    addLink = (event) => {
        event.preventDefault();
        const videoLinkRegex = new RegExp('(youtu\.be|youtube.com|vimeo.com)\/');
        const docType = videoLinkRegex.test(this.url) ? 'movie' : 'link';
        $.ajax({
            cache: false,
            url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
            type: 'POST',
            data: {
                act: 'AddAid',
                addAids: this.url,
                meetingId: this.props.meetingUid,
                assetName: this.title,
                assetDocType: docType,
                assetType: 'AID',
                section: 'additional-resources',
                a: Date.now()
            },
            success: function (result) {
                window.vtkTrackerPushAction('AddAdditionalResource');
                location.reload();
            }
        });
    }

    public render() {
        let { assets, helper, user_variable, videos, meetingId, section } = this.props;
        console.log('videos', videos);

        const id = section.replace(' ', '-').toLowerCase();
        const links = meetingId in videos ? this.props.videos[meetingId] : [];
        let assetsAll = assets.concat(links);
        assetsAll = assetsAll.filter(asset => asset['section'] === id);
        const permissionCheck = HAS_PERMISSION_FOR()("vtk_troop_haspermision_edit_yearplan_id");

        return (
            <div>
                <VtkContent idName={id}>
                    <h6>{section}</h6>
                    <ul className="__list_of_assets large-block-grid-2 medium-block-grid-2 small-block-grid-2">
                        {assetsAll.map((link) => {
                            return asset(link, user_variable.user_current_year !== user_variable.vtk_current_year)
                        })}
                    </ul>
                    {permissionCheck && (id === 'meeting-aids' ?
                        <a className="add-btn" data-reveal-id="modal_popup" data-reveal-ajax="true" href={`/content/girlscouts-vtk/controllers/vtk.include.modals.modal_meeting_aids.html?elem=${helper.currentDate}`} title={`Add ${section}`}>
                            <i className="icon-button-circle-plus"></i>Add {section}
                        </a>
                        : <div className="toggle-container">
                            <label className="toggle-element" htmlFor="check">
                                <a className="add-btn" title={`Add ${section}`}>
                                    <i className="icon-button-circle-plus"></i>Add {section}
                                </a>
                            </label>
                            <input id="check" type="checkbox"></input>
                            <form className="toggle-target" id="addLink">
                                <section className="row">
                                    <div className="column medium-10">
                                        <input type="text" placeholder="Title" id="title" onChange={e => this.title = e.target.value}/>
                                    </div>
                                    <div className="column medium-10">
                                        <input type="text" placeholder="Url" id="url" onChange={e => this.url = e.target.value}/>
                                    </div>
                                    <button type="submit" className="btn" onClick={this.addLink}>Add</button>
                                </section>
                            </form>
                        </div>
                    )}
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
        meetingId: state.meetingEvents.meetingInfo.id,
        meetingUid: state.meetingEvents.uid
    }
};

export default connect(mapStateToProps)(VtkMtgPlanAids)



