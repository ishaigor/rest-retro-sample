/**
 * Copyright 2014 the original author or authors.
 * 
 * This file is part of Retro-fitting Security into REST web services sample (the sample).
 * The sample is part of the talk presented at Java One 2014 
 * 	https://oracleus.activeevents.com/2014/connect/sessionDetail.ww?SESSION_ID=1765&amp;tclass=popup
 * 
 * The sample is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The sample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the sample.  If not, see <http://www.gnu.org/licenses/>.
 */
var wordsApp = angular.module('wordsApp', ['infinite-scroll']);

wordsApp.controller('WordsController', function($scope, WordsService,$http) {
  $scope.wordsService = new WordsService();
  $http.defaults.headers.common.Authorization = 'Bearer ' + $("meta[name='_bearer']").attr("content");
});

// WordsService constructor function to encapsulate HTTP and pagination logic
wordsApp.factory('WordsService', function($http) {
  var WordsService = function() {
    this.items = [];
    this.busy = false;
    this.after = 0;
  };

  WordsService.prototype.nextPage = function() {
    if (this.busy) return;
    this.busy = true;

    var url = "http://localhost:8080/secure/service/v1.0/word/list?from=" + this.after 
    	+ "&to="+ (this.after + 50) 
    	;
    $http
    	.get(url)
    	.success(function(data, status, headers, config) {
            console.log("got data");
		      for (var i = 0; i < data.length; i++) {
		        this.items.push(data[i]);
		      }
		      this.after = this.after + this.items.length;
		      this.busy = false;
    	}
    	.bind(this)
    	)
    	.error(function(data, status, headers, config) {
            console.log("error");
        })
    	;
  };

  return WordsService;
});