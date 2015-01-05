<li  className="row meeting " key={obj[comment].id} >
  <div className="column large-20 medium-20 large-centered medium-centered">
    
    <div className="large-3 medium-3 small-4 columns">
      <div className="bg-square  ">
        <div className="count"></div>
        <div className="date">
          <p className="month">{ moment(comment).get('year') < 1978 ? "" : moment(comment).format('MMM')}</p>
          <p className="day">{ moment(comment).get('year') < 1978 ? "" : moment(comment).format('DD')}</p>
          <p className="hour">{ moment(comment).get('year') < 1978 ? "" : moment(comment).format('hh:mm a')}</p>
        </div>
      </div>
    </div>    

    <div className="large-22 medium-22 small-17 columns">
      <p className="subtitle">
      	{obj[comment].name}
      </p>
     	<p>{obj[comment].activityDescription}</p>
    </div>
    <div className="large-2 medium-2 columns hide-for-small">
 
    </div>
  </div>

</li>
