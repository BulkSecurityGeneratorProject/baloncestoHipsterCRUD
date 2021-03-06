'use strict';

angular.module('ligabaloncestoApp').controller('LigaDialogController',
    ['$scope', '$stateParams', '$modalInstance', 'entity', 'Liga', 'Arbitro', 'Temporada',
        function($scope, $stateParams, $modalInstance, entity, Liga, Arbitro, Temporada) {

        $scope.liga = entity;
        $scope.arbitros = Arbitro.query();
        $scope.temporadas = Temporada.query();
        $scope.load = function(id) {
            Liga.get({id : id}, function(result) {
                $scope.liga = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('ligabaloncestoApp:ligaUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.liga.id != null) {
                Liga.update($scope.liga, onSaveFinished);
            } else {
                Liga.save($scope.liga, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
