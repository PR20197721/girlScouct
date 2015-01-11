<li  className="row meeting activity" key={obj[comment].id} >
  <div className="column large-20 medium-20 large-centered medium-centered">
    
    <div className="large-3 medium-3 small-4 columns">
      <div className="bg-square  ">
        <div className="date">
          <p className="month">{ moment(comment).get('year') < 1978 ? "" : moment(comment).format('MMM')}</p>
          <p className="day">{ moment(comment).get('year') < 1978 ? "" : moment(comment).format('DD')}</p>
          <p className="hour">{ moment(comment).get('year') < 1978 ? "" : moment(comment).format('hh:mm a')}</p>
        </div>
      </div>
    </div>    

    <div className="large-22 medium-22 small-17 columns">
      <p className="subtitle">
      	<ViewMeeting date={comment} name={obj[comment].name}/>
      </p>
     	<p class="category">{obj[comment].content}</p>
     	<p class="blurb">{obj[comment].locationName}</p>
      {/* <br/>Location:{obj[comment].locationName} --- {obj[comment].locationAddress} -- {obj[comment].locationRef}
        <br/>Cost: {obj[comment].cost}
        <br/>Desc: {obj[comment].activityDescription}
        <br/>Desc: {obj[comment].content}
        <br/>Path: {obj[comment].path}
        <br/>Uid: {obj[comment].uid}
        <br/>Register Url{obj[comment].registerUrl}
        <br/>Canceled:{obj[comment].cancelled}
        <br/>IsEditable: {obj[comment].isEditable}*/}
     	
    </div>
    <div className="large-2 medium-2 columns hide-for-small">
 
    </div>
  </div>

</li>
