<li  className="row meeting " key={obj[comment].id} id={obj[comment].id +1}>
  <div className="column large-20 medium-20 large-centered medium-centered">
    
    <div className="large-3 medium-3 small-4 columns">
      <div className="bg-square  ">
        <div className="count">{(obj[comment].id+1)}</div>
        <div className="date">
          <p className="month">{moment(comment).format('MMM')}</p>
          <p className="day">{moment(comment).format('DD')}</p>
          <p className="hour">{moment(comment).format('hh:mm a')}</p>
        </div>
      </div>
    </div>    

    <div className="large-22 medium-22 small-17 columns">
      <p className="subtitle">{ obj[comment].meetingInfo.name}</p>
      <p className="category">{obj[comment].meetingInfo.cat}</p>
      <p className="blurb">{obj[comment].meetingInfo.blurb}</p>
    </div>
    <div className="large-2 medium-2 columns hide-for-small">
      <MeetingImg mid={obj[comment].meetingInfo.id}/>
    </div>
  </div>

</li>
