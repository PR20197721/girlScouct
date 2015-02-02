<li  className="row meeting ui-state-default" key={obj[comment].id} id={obj[comment].id+1}>
   <img className="touchscroll" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/touchscroll-small.png" width="21" height="34"/>
      
  <div className="column large-20 medium-20 large-centered medium-centered">
   
    <div className="large-3 medium-3 small-4 columns">
      <div className={testrr(obj, comment)}>
    
        <div className={ (moment(comment).get('year') < 1978) ?  "hide" : "count"}>{(obj[comment].id)+1}</div>      
        <div className="date">
          <p className="month">{ moment(comment).get('year') < 1978 ? "meeting" : moment(comment).format('MMM')}</p>
          <p className="day">{ moment(comment).get('year') < 1978 ? (obj[comment].id)+1 : moment(comment).format('DD')}</p>
          <p className="hour">{ moment(comment).get('year') < 1978 ? "" : moment(comment).format('hh:mm a')}</p>
        </div>
      </div>
    </div>    

    <div className="large-22 medium-22 small-24 columns">
      <p className="subtitle">
      	<ViewMeeting date={comment} name={obj[comment].meetingInfo.name}/>
      </p>
      <p className="category">{obj[comment].meetingInfo.cat}</p>
      <p className="blurb">{obj[comment].meetingInfo.blurb}</p>
    </div>
    <div className="large-2 medium-2 columns hide-for-small">
      <MeetingImg mid={obj[comment].meetingInfo.id}/>
    </div>
  </div>
</li>
