import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IPoint } from 'app/shared/model/point.model';
import { getEntity, updateEntity, createEntity, reset } from './point.reducer';

export const PointUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const pointEntity = useAppSelector(state => state.point.entity);
  const loading = useAppSelector(state => state.point.loading);
  const updating = useAppSelector(state => state.point.updating);
  const updateSuccess = useAppSelector(state => state.point.updateSuccess);

  const handleClose = () => {
    navigate('/point' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...pointEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...pointEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="khReactApp.point.home.createOrEditLabel" data-cy="PointCreateUpdateHeading">
            Create or edit a Point
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="point-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Title"
                id="point-title"
                name="title"
                data-cy="title"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  minLength: { value: 5, message: 'This field is required to be at least 5 characters.' },
                  maxLength: { value: 20, message: 'This field cannot be longer than 20 characters.' },
                }}
              />
              <ValidatedField label="Description" id="point-description" name="description" data-cy="description" type="text" />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/point" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default PointUpdate;
