<li  className="row meeting " key={obj.id} id={obj.id}>
  <div className="column large-20 medium-20 large-centered medium-centered">
    
    <div className="large-3 medium-3 small-4 columns">
      <div className="bg-square  ">
        <div className="count">{(obj.id+1)}</div>
        <div className="date">
          <p className="month">{moment(date).format('MMM')}</p>
          <p className="day">{moment(date).format('DD')}</p>
          <p className="hour">{moment(date).format('hh:mm a')}</p>
        </div>
       
      </div>
    </div>    

    <div className="large-22 medium-22 small-17 columns">
      <p className="subtitle">
        
        {obj.title}
      </p>

      <p className="category">{obj.meetingInfo.cat}</p>
      <p className="blurb">{obj.meetingInfo.blurb}</p>

    </div>
    <div className="large-2 medium-2 columns hide-for-small">
      <img src={img} alt={obj.meetingInfo.id}/>
    </div>

  </div>

</li>
