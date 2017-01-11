<li className="row milestone">
  <div className="column large-20 medium-20 large-centered medium-centered">
    <span>{ moment(comment).get('year') < 1978 ? "" : moment(comment).format('MM/DD/YY')} {obj[comment].blurb}</span>
  </div>
</li>
